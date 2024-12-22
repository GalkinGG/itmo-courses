#!/bin/bash

size=$1
name="files/file$2"
count=0
while read -r tmp && [ $count -lt $size ]; do
    echo -en "$((tmp * 2))\n" >> $name
    ((count++))
done < $name
