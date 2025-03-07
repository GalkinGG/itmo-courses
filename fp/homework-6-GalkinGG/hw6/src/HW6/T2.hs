{-# LANGUAGE DataKinds #-}
{-# LANGUAGE TypeFamilies #-}
{-# LANGUAGE TypeOperators #-}
{-# LANGUAGE UndecidableInstances #-}

module HW6.T2
  ( TSet
  , Contains
  , Add
  , Delete
  ) where

import GHC.TypeLits

type TSet = [Symbol]

type family Contains (name :: Symbol) (set :: TSet) :: Bool where
  Contains _ '[] = 'False
  Contains name (name ': _) = 'True
  Contains name (_ ': rest) = Contains name rest

type family Delete (name :: Symbol) (set :: TSet) :: TSet where
  Delete _ '[] = '[]
  Delete name (name ': rest) = rest
  Delete name (x ': rest) = x ': Delete name rest

type family Add (v :: Symbol) (set :: TSet) :: TSet where
  Add v set = v ': Delete v set
