#!/bin/bash

display_time() {
    date +%F_%T
}

mkdir test && echo "catalog test was created successfully" > report && touch test/$(display_time)
ping -c 1 www.net_nikogo.ru || echo "$(display_time) host is unavailable now" >> report
