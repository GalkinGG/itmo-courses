module HW1.T2
  ( N (..)
  , nplus
  , nmult
  , nsub
  , nFromNatural
  , nToNum
  , ncmp
  , nEven
  , nOdd
  , ndiv
  , nmod
  ) where

import Numeric.Natural

data N = Z | S N

nplus :: N -> N -> N
nplus Z n = n
nplus (S n) m = S (nplus n m)

nmult :: N -> N -> N
nmult Z _ = Z
nmult (S n) m = nplus m (nmult n m)

nsub :: N -> N -> Maybe N
nsub Z Z = Just Z
nsub Z _ = Nothing
nsub (S n) Z = Just (S n)
nsub (S n) (S m) = nsub n m

ncmp :: N -> N -> Ordering
ncmp Z Z = EQ
ncmp Z _ = LT
ncmp _ Z = GT
ncmp (S n) (S m) = ncmp n m

nFromNatural :: Natural -> N
nFromNatural 0 = Z
nFromNatural n = S (nFromNatural (n - 1))

nToNum :: Num a => N -> a
nToNum Z = 0
nToNum (S n) = 1 + nToNum n

nEven :: N -> Bool
nEven Z = True
nEven (S Z) = False
nEven (S (S n)) = nEven n

nOdd :: N -> Bool
nOdd Z = False
nOdd (S Z) = True
nOdd (S (S n)) = nOdd n

ndiv :: N -> N -> N
ndiv _ Z = error "division by zero"
ndiv Z _ = Z
ndiv n m =
  case nsub n m of
    Nothing -> Z
    Just n' -> S (ndiv n' m)

nmod :: N -> N -> N
nmod _ Z = error "modulo by zero"
nmod n m =
  case nsub n m of
    Nothing -> n
    Just n' -> nmod n' m
