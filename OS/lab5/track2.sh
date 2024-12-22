#!/bin/bash

log_file="2.tracklog"

if [[ -f $log_file ]]; then
  rm $log_file
fi

./mem.bash &
./mem2.bash &

while true; do
  top -b -n 1 | head -n 5 | tail -n 2 | awk 'BEGIN {FS=","} {print $2 $5}' >> "freemem1"
  top_table=`top -b -n 1`
  info=`grep "track-2.sh" <<< $top_table`
  if [[ -z `grep "mem[2]*.bash" <<< $top_table` ]]; then break; fi
  date +%d.%m.%Y\ %H:%M:%S
  awk '{print $0}' <<< $top_table | head -n 5 | tail -n 2
  echo $info
  awk '{print $0}' <<< $top_table | head -n 12 | tail -n 5
  echo
  sleep 1
done >> $log_file

sudo dmesg | grep "mem[2]*.bash" 
