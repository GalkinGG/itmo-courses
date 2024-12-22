#!/bin/bash

for ((i = 1; i <= $2; i++)); do
  ./proc.sh $1 $i &
done
wait