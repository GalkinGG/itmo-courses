#!/bin/bash

cnt=$(<.size)

for ((i=1; i <= $1; i++)); do
    name="files/file$i"
    echo -n "" > "$name"
    for ((j=1; j<=$cnt; j++)); do
        echo -en "$(($j % 10))\n" >> "$name"
    done
done
