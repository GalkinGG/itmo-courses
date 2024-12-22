#!/bin/bash

x=$1 
N=1000

result=1
term=1

for ((n=1; n<N; n++)); do
    term=$(echo "scale=9; $term*$x/$n" | bc)
    result=$(echo "scale=9; $result+$term" | bc)
done

echo "$result"

