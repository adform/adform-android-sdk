echo "Execution completed"
# say "Execution completed in $DIFF seconds"
# Play complete sound
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r
