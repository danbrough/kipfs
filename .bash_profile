
# Get the aliases and functions
[ -f $HOME/.bashrc ] && . $HOME/.bashrc
### START-Keychain ###
# Let  re-use ssh-agent and/or gpg-agent between logins
/usr/bin/keychain  $HOME/.ssh/id_rsa
#/usr/bin/keychain --timeout 1440 $HOME/.ssh/id_rsa
source $HOME/.keychain/$HOSTNAME-sh
### End-Keychain ###

