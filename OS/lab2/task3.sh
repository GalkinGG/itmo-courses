#!/bin/bash

ps ax -o pid,start_time --sort=start_time | tail -n1 | awk '{print $1}'
