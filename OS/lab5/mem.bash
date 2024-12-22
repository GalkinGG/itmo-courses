#!/bin/bash

report="report.log"
array=()
i=0

while true; do
  array+=(0 1 2 3 4 5 6 7 8 9)
  let i=$i+1
  if [[ $i -eq 100000 ]]; then
    echo "${#array[*]}" > $report
    i=0
  fi
done
