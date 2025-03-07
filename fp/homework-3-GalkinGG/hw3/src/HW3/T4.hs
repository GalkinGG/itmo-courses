module HW3.T4
  ( State (..)
  , Prim (..)
  , Expr (..)
  , mapState
  , wrapState
  , joinState
  , modifyState
  , eval
  ) where

import HW3.T1
import qualified Control.Monad

newtype State s a = S { runS :: s -> Annotated s a }

mapState :: (a -> b) -> State s a -> State s b
mapState f (S g) = S $ \s -> case g s of
    a :# s' -> f a :# s'

wrapState :: a -> State s a
wrapState a = S $ \s -> a :# s

joinState :: State s (State s a) -> State s a
joinState (S f) = S $ \s -> case f s of
    S g :# s' -> g s'

modifyState :: (s -> s) -> State s ()
modifyState f = S $ \s -> () :# f s

instance Functor (State s) where
  fmap = mapState

instance Applicative (State s) where
  pure = wrapState
  p <*> q = Control.Monad.ap p q

instance Monad (State s) where
  m >>= f = joinState (fmap f m)

data Prim a =
    Add a a
  | Sub a a
  | Mul a a
  | Div a a
  | Abs a
  | Sgn a
  deriving Show

data Expr = Val Double | Op (Prim Expr)
  deriving Show

instance Num Expr where
  x + y = Op (Add x y)
  x - y = Op (Sub x y)
  x * y = Op (Mul x y)
  abs x = Op (Abs x)
  signum x = Op (Sgn x)
  fromInteger x = Val (fromInteger x)

instance Fractional Expr where
  x / y = Op (Div x y)
  fromRational x = Val (fromRational x)

-- Helper function for binary operations
evalBinary :: (Double -> Double -> Double) -> (Double -> Double -> Prim Double) -> Expr -> Expr -> State [Prim Double] Double
evalBinary op prim x y = do
    a <- eval x
    b <- eval y
    modifyState (prim a b :)
    pure (a `op` b)

-- Helper function for unary operations
evalUnary :: (Double -> Double) -> (Double -> Prim Double) -> Expr -> State [Prim Double] Double
evalUnary op prim x = do
    a <- eval x
    modifyState (prim a :)
    pure (op a)

eval :: Expr -> State [Prim Double] Double
eval (Val x)          = pure x
eval (Op (Add x y))   = evalBinary (+) Add x y
eval (Op (Sub x y))   = evalBinary (-) Sub x y
eval (Op (Mul x y))   = evalBinary (*) Mul x y
eval (Op (Div x y))   = evalBinary (/) Div x y
eval (Op (Abs x))     = evalUnary abs Abs x
eval (Op (Sgn x))     = evalUnary signum Sgn x
