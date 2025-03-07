{-# LANGUAGE DerivingStrategies #-}
{-# LANGUAGE GeneralisedNewtypeDeriving #-}

module HW4.T2
  ( ParseError (..)
  , runP
  , pChar
  , parseError
  , parseExpr
  ) where
  
import Numeric.Natural (Natural)
import Control.Applicative
import Control.Monad
import Data.Char (isDigit, isSpace, digitToInt)

import HW4.Types
import HW4.T1 (ExceptState(..))

data ParseError = ErrorAtPos Natural
  deriving Show

newtype Parser a = P (ExceptState ParseError (Natural, String) a)
  deriving newtype (Functor, Applicative, Monad)

runP :: Parser a -> String -> Except ParseError a
runP (P es) input = case runES es (0, input) of
  Error e             -> Error e
  Success (result :# _) -> Success result

pChar :: Parser Char
pChar = P $ ES $ \(pos, s) ->
  case s of
    []     -> Error (ErrorAtPos pos)
    (c:cs) -> Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _) -> Error (ErrorAtPos pos)

instance Alternative Parser where
  empty = parseError
  (P p1) <|> (P p2) = P $ ES $ \state ->
    case runES p1 state of
      Error _ -> runES p2 state
      success -> success

instance MonadPlus Parser

parseExpr :: String -> Except ParseError Expr
parseExpr input = runP ((withWhitespace pAddSub) <* pEof) input

pEof :: Parser ()
pEof = P $ ES $ \(pos, s) ->
  case s of
    [] -> Success (() :# (pos, s))
    _  -> Error (ErrorAtPos pos)

pWhitespace :: Parser ()
pWhitespace = void $ many (mfilter isSpace pChar)

withWhitespace :: Parser a -> Parser a
withWhitespace p = pWhitespace *> p <* pWhitespace

pCharSymbol :: Char -> Parser Char
pCharSymbol c = withWhitespace $ mfilter (== c) pChar

pDouble :: Parser Expr
pDouble = do
  integerPart <- parseIntegerPart
  fractionalPart <- parseFractionalPart
  return $ combineParts integerPart fractionalPart
  where
    parseIntegerPart :: Parser Integer
    parseIntegerPart = fmap stringToInt (some (mfilter isDigit pChar))

    parseFractionalPart :: Parser Rational
    parseFractionalPart = maybe 0 id <$> optional (do
      void pChar  -- dot
      digits <- some (mfilter isDigit pChar)
      return (stringToFraction digits))

    combineParts :: Integer -> Rational -> Expr
    combineParts int frac = fromRational (toRational int + frac)

    stringToInt :: String -> Integer
    stringToInt = foldl (\acc d -> acc * 10 + fromIntegral (digitToInt d)) 0

    stringToFraction :: String -> Rational
    stringToFraction s = toRational (stringToInt s) / (10 ^ length s)

binaryOp :: Char -> (Expr -> Expr -> Prim Expr) -> Parser (Expr -> Expr -> Expr)
binaryOp symbol prim = pCharSymbol symbol *> pure (\x y -> Op (prim x y))

pAddSub :: Parser Expr
pAddSub = chainl1 pMulDiv (binaryOp '+' Add <|> binaryOp '-' Sub)

pMulDiv :: Parser Expr
pMulDiv = chainl1 pFactor (binaryOp '*' Mul <|> binaryOp '/' Div)

pFactor :: Parser Expr
pFactor = pDouble
      <|> withWhitespace (pCharSymbol '(' *> pAddSub <* pCharSymbol ')')

chainl1 :: Parser a -> Parser (a -> a -> a) -> Parser a
chainl1 p op = do
  x <- p
  rest x
  where
    rest x = (do f <- op
                 y <- p
                 rest (f x y))
          <|> pure x