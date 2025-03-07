module HW2.T3
  ( epart
  , mcat
  ) where

mcat :: Monoid a => [Maybe a] -> a
mcat = foldMap (maybe mempty id)

epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart = foldMap (either (\x -> (x, mempty)) (\y -> (mempty, y)))