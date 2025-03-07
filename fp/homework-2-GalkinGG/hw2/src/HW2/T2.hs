module HW2.T2
  ( joinWith
  , splitOn
  ) where

import Data.List.NonEmpty (NonEmpty(..))

splitOn :: Eq a => a -> [a] -> NonEmpty [a]
splitOn sep = foldr go ([] :| [])
  where
    go x (y :| ys)
      | x == sep  = [] :| (y:ys)
      | otherwise = (x : y) :| ys

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep (x :| xs) = foldl (\acc l -> acc ++ sep : l) x xs