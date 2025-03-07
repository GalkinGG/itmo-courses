module HW1.T3
  ( Tree (..)
  , tsize
  , tdepth
  , tmember
  , tinsert
  , tFromList
  ) where

type Meta = Int

data Tree a = Leaf | Branch Meta (Tree a) a (Tree a)
  deriving (Show)

tsize :: Tree a -> Int
tsize Leaf = 0
tsize (Branch size _ _ _) = size

tdepth :: Tree a -> Int
tdepth Leaf = 0
tdepth (Branch _ left _ right) = 1 + max (tdepth left) (tdepth right)

tmember :: Ord a => a -> Tree a -> Bool
tmember _ Leaf = False
tmember x (Branch _ left y right)
  | x < y     = tmember x left
  | x > y     = tmember x right
  | otherwise = True

-- Helper function to create a new branch and calculate its size.
mkBranch :: Tree a -> a -> Tree a -> Tree a
mkBranch left x right = Branch (1 + tsize left + tsize right) left x right

tinsert :: Ord a => a -> Tree a -> Tree a
tinsert x Leaf = mkBranch Leaf x Leaf
tinsert x (Branch _ left y right)
  | x < y     = mkBranch (tinsert x left) y right
  | x > y     = mkBranch left y (tinsert x right)
  | otherwise = Branch (tsize left + tsize right + 1) left y right

tFromList :: Ord a => [a] -> Tree a
tFromList = foldr tinsert Leaf
