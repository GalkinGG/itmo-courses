#!/bin/bash

temp_dir=$(mktemp -d)

find . -type f | while read file 
do
    hash=$(md5sum "$file" | awk '{print $1}')

    if grep -q -s "$hash" "$temp_dir/hash_list.txt"
    then
        echo "$file" >> "$temp_dir/$hash/files.txt"
    else
        mkdir -p "$temp_dir/$hash"
        echo "$hash $file" >> "$temp_dir/hash_list.txt"
        echo "$file" >> "$temp_dir/$hash/files.txt"
    fi
done

find "$temp_dir" -mindepth 1 -type d | while read group 
do
    echo "Группа схожих файлов:"
    cat "$group/files.txt"
    echo "------"
done

rm -rf "$temp_dir"
