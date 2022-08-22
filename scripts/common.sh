
set_gradle_prop(){
  sed -i gradle.properties  -e 's|^'$1'=.*|'$1'='$2'|g'
}

get_gradle_prop(){
  cat gradle.properties  | sed -n -e '/'$1'=/p' | sed -e 's|'$1'=||g'
}

is_mac() {
  if [ "$(uname)" = "Darwin" ]; then
    return 0
  else
    return 1
  fi
}


message_prompt(){
  while true; do
    read -p "$1: " yn
    case $yn in
    [Yy]*) return 0 ;;
    [Nn]*) return 1 ;;
    *) echo "Please answer yes or no." ;;
    esac
  done
}

