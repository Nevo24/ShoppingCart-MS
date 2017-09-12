#!/bin/bash


# Global Variables #

wait_for=30
files=""
usage() {
  echo "$0 -n k8snamespace [-s seconds to wait ] [-f comma separated File names]"
}


# ARGS #

while getopts :n:s:f: arg ;do
  case "$arg" in
    n) ns=$OPTARG;;
    s) wait_for=$OPTARG;;
    f) files=$OPTARG;;
    *) echo "Unknown Option $arg"
       usage
       exit 6;;
  esac
done


# Vars #

current_wait=0
pod_tmp_file="/tmp/pod_status_${ns}.tmp"

#
# Functions #
#
die() { echo >&2 -e "\n [ERROR] : $@\n"; exit 1; }
run() { echo "Running: $@"; "$@" ;STEP=$@ code=$?; [ $code -ne 0 ] && die "STEP $STEP failed with error code $code" || echo "STEP $STEP has completed." ; }
report() {
  if [[ $status -eq 0 ]] ;then
    pattern='kdsfjklasdjfasdjklfjsdklajfasdjfjdskl'
    text='Final state of Pod:'
        echo ""
  else
    pattern='true;map\[running'
    text='Waiting for Pod:'
  fi
  cat $pod_tmp_file|grep -v $pattern | while read line ; do
    echo "$text `echo $line|awk -F';' '{print $1}'`, container name:`echo $line|awk -F';' '{print $2}'`. Ready state is `echo $line |awk -F';' '{print $3}'` and Current State is `echo $line|awk -F';' '{print $4 }'|sed 's/map//g'`"
  done
}


# Init #

echo "Args: Wait time - $wait_for Files List - $files"
if [[ -z "$ns" ]] ;then
  echo "Namespace option -n not set"
  usage
  exit
fi


# Main #


for file in `echo $files|sed  's/,/ /g'`; do
  run kubectl apply -f $file -n=$ns
done

list=`kubectl get po -n=$ns --no-headers -o name`


while [[ ${wait_for} -gt ${current_wait} ]] ; do
  rm -f ${pod_tmp_file}
  status=0
  for pod in ${list} ;do
    content="$pod"
        content_1=`kubectl get $pod -n=${ns} -o jsonpath={range.status.containerStatuses[*]}{.name}";"{.ready}";"{.state}"^"{end}`
        for _content_1 in `echo $content_1|sed 's/\^/\n/g'|sed '/^$/d'` ; do
          echo "${content};${_content_1}" >>${pod_tmp_file}
        done
  done
  while read content_1_0 ;do
        content_1_1=`echo $content_1_0|awk -F';' '{print $3}'`
    content_1_2=`echo $content_1_0|awk -F';' '{print $4}'`
    echo $content_1_1|grep -q 'true'
    r2=$?
    echo $content_1_2|grep -q 'running'
    r1=$?
    if [[ $r1 -ne 0 ]] || [[ $r2 -ne 0 ]] ;then
      status=1
    fi
  done <${pod_tmp_file}
  report
  if [[ $status -eq 0 ]] ;then
    let current_wait=wait_for+1
  else
    let current_wait=current_wait+5
        echo "Waiting for 5 seconds..."
    sleep 5
  fi
done

if [[ "$status" -ne 0 ]] ; then
  status=0
  report
  exit 6
fi
