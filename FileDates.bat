@echo ">>> Start" > fileDates.log
java FileDates.FileDates -o FileDates\FileDates.properties -c 2013-09-01 -p testdata      >> fileDates.log
java FileDates.FileDates -o FileDates\FileDates.properties -c 2011-09-01 -p testdata\2    >> fileDates.log
java FileDates.FileDates -o FileDates\FileDates.properties -c 2009-09-01 -p testdata\2\3a >> fileDates.log
java FileDates.FileDates -o FileDates\FileDates.properties -c 2007-09-01 -p testdata\2\3a\4b >> fileDates.log
@echo ">>> finished" >> fileDates.log