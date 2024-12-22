#!/bin/bash

for ((i = 1; i <= $1; i++)); do
  ./ex.sh $i &
done
wait