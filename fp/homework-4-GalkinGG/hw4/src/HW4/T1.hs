module HW4.T1
  ( EvaluationError (..)
  , ExceptState (..)
  , mapExceptState
  , wrapExceptState
  , joinExceptState
  , modifyExceptState
  , throwExceptState
  , eval
  ) where

import qualified Control.Monad
import HW4.Types

data ExceptState e s a = ES
  { runES :: s -> Except e (Annotated s a) }

-- helper function from hw3
mapExcept :: (a -> b) -> (Except e a -> Except e b)
mapExcept _ (Error e)   = Error e
mapExcept f (Success x) = Success (f x)

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f (ES run) = ES $ \s -> mapExcept (\(a :# s') -> f a :# s') (run s)

wrapExceptState :: a -> ExceptState e s a
wrapExceptState a = ES $ \s -> Success (a :# s)

-- helper function from hw3
joinExcept :: Except e (Except e a) -> Except e a
joinExcept (Error e) = Error e
joinExcept (Success (Error e)) = Error e
joinExcept (Success (Success x)) = Success x

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState (ES runOuter) = ES $ \s -> 
  joinExcept $ mapExcept (\(inner :# s') -> runES inner s') (runOuter s)

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES $ \s -> Success (() :# f s)

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES $ \_ -> Error e

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  (<*>) p q = Control.Monad.ap p q

instance Monad (ExceptState e s) where
  (>>=) m f = joinExceptState (fmap f m)

data EvaluationError = DivideByZero
    deriving Show

evalUnary :: (Double -> Double)
          -> (Double -> Prim Double)
          -> Expr
          -> ExceptState EvaluationError [Prim Double] Double
evalUnary op prim x = do
  a <- eval x
  modifyExceptState (prim a :)
  pure (op a)

safeDiv :: Double -> Double -> ExceptState EvaluationError [Prim Double] Double
safeDiv _ 0 = throwExceptState DivideByZero
safeDiv a b = pure (a / b)

evalBinarySafe :: (Double -> Double -> ExceptState EvaluationError [Prim Double] Double)
               -> (Double -> Double -> Prim Double)
               -> Expr
               -> Expr
               -> ExceptState EvaluationError [Prim Double] Double
evalBinarySafe safeOp prim x y = do
  a <- eval x
  b <- eval y
  modifyExceptState (prim a b :)
  safeOp a b

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval (Val x)          = pure x
eval (Op (Add x y))   = evalBinarySafe (\a b -> pure (a + b)) Add x y
eval (Op (Sub x y))   = evalBinarySafe (\a b -> pure (a - b)) Sub x y
eval (Op (Mul x y))   = evalBinarySafe (\a b -> pure (a * b)) Mul x y
eval (Op (Div x y))   = evalBinarySafe safeDiv Div x y
eval (Op (Abs x))     = evalUnary abs Abs x
eval (Op (Sgn x))     = evalUnary signum Sgn x