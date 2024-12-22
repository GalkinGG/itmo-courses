#!/bin/bash

array=()

while [[ $i -le $1 ]]; do
  array+=(0 1 2 3 4 5 6 7 8 9)
  let i=$i+10
done
