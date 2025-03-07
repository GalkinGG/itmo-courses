module HW0.T4
  ( fac
  , fib
  , map'
  , repeat'
  ) where

import Numeric.Natural (Natural)
import Data.Function (fix)

repeat' :: a -> [a]
repeat' x = fix (x:)

map' :: (a -> b) -> [a] -> [b]
map' = fix (\r f xs -> case xs of
    [] -> []
    (y:ys) -> f y : r f ys)

fibCalc :: Natural -> Natural -> Natural -> Natural
fibCalc = fix (\f prev curr n -> if n == 0 then curr else f curr (prev + curr) (n - 1))

fib :: Natural -> Natural
fib = fibCalc 1 0

fac :: Natural -> Natural
fac = fix (\r n -> if n == 0 then 1 else n * r (n - 1))
