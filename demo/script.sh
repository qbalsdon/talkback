#!/bin/sh
DELAY=0.1
function typeWord {
    word="$1"
    grep -o . <<< "${word}" | while read letter; do
        printf "${letter}"
        sleep ${DELAY} 
    done
}


function exe {
    command="$1"

    for word in ${command}; do
        typeWord "${word}"
        printf " "
        sleep ${DELAY}
    done

    printf "\n"
    eval "$command"
}

exe "echo \"Hello world\""
