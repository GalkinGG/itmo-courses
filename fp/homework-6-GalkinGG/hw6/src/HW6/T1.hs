module HW6.T1
  ( BucketsArray
  , CHT (..)
  
  , newCHT
  , getCHT
  , putCHT
  , sizeCHT

  , initCapacity
  , loadFactor
  ) where

import Control.Monad (forM_, when)
import Control.Concurrent.Classy (STM, MonadConc, atomically, MonadSTM(newTVar, writeTVar))
import Control.Concurrent.Classy.STM (TArray, TVar)
import Control.Concurrent.Classy.STM.TVar (readTVar)
import Data.Array.MArray (newArray, readArray, writeArray)
import Data.Array.Base (MArray, getNumElements)
import Data.Hashable (Hashable, hash)

initCapacity :: Int
initCapacity = 16

loadFactor :: Double
loadFactor = 0.75

type Bucket k v = [(k, v)]
type BucketsArray stm k v = TArray stm Int (Bucket k v)

data CHT stm k v = CHT
  { chtBuckets :: TVar stm (BucketsArray stm k v)
  , chtSize    :: TVar stm Int
  }

newCHT :: MonadConc m => m (CHT (STM m) k v)
newCHT = atomically $ do
  initialBuckets <- newArray (0, initCapacity - 1) []
  buckets <- newTVar initialBuckets
  sizeVar <- newTVar 0
  return $ CHT buckets sizeVar

bucketIndex :: Hashable k => k -> Int -> Int
bucketIndex key capacity = hash key `mod` capacity

getBucket 
  :: ( MArray a e m
     , Hashable k
     ) => k 
     -> a Int e 
     -> m (Int, e)
getBucket key buckets = do
  capacity <- getCapacity buckets
  let index = bucketIndex key capacity
  bucket <- readArray buckets index
  return (index, bucket)

getCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> CHT (STM m) k v
  -> m (Maybe v)
getCHT key cht = atomically $ do
  buckets <- readTVar (chtBuckets cht)
  (_, bucket) <- getBucket key buckets
  return $ lookup key bucket

putCHT
  :: ( MonadConc m
     , Hashable k
     )
  => k
  -> v
  -> CHT (STM m) k v
  -> m ()
putCHT key val cht = atomically $ do
  buckets <- readTVar (chtBuckets cht)
  (index, bucket) <- getBucket key buckets
  let (prefix, suffix) = span ((/= key) . fst) bucket

  writeArray buckets index $ (key, val) : prefix ++ drop 1 suffix

  when (null suffix) $ do
    oldSize <- readTVar (chtSize cht)
    writeTVar (chtSize cht) $ oldSize + 1

    oldCapacity <- getCapacity buckets

    when (fromIntegral (oldSize + 1) >= fromIntegral oldCapacity * loadFactor) $ do
      let newCapacity = oldCapacity * 2
      newArr <- newArray (0, newCapacity - 1) []
      forM_ [0 .. oldCapacity - 1] $ \i -> do
        bucket' <- readArray buckets i
        forM_ bucket' $ \(key', val') -> do
          let newBucketIndex = bucketIndex key' newCapacity
          bucketNew <- readArray newArr newBucketIndex
          writeArray newArr newBucketIndex $ (key', val') : bucketNew
      writeTVar (chtBuckets cht) newArr


sizeCHT :: MonadConc m => CHT (STM m) k v -> m Int
sizeCHT cht = atomically $ readTVar (chtSize cht)

getCapacity :: MArray a e m => a Int e -> m Int
getCapacity buckets = getNumElements buckets