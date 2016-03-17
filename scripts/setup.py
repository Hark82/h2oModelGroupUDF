import os

# Add to the start of a file
def line_prepender(filename, line):
	with open(filename, 'r+') as f:
		content = f.read()
		.seek(0, 0)
		f.write(line.rstrip('\r\n') + '\n' + content)

def main():
	# os.listdir()
	# for each model
	#	prepend package
	#	store model name

	# filewriter to create ai.h2o.hive.udf.Model.java
	# for each model
	#	add to String[] in Java class
