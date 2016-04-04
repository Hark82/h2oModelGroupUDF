import os
import sys

modelsdir = "../src/main/java/ai/h2o/hive/udf/models"
modelnames = []

# Add to the start of a file
def line_prepender(filename, line):
	with open(filename, 'r+') as f:
		content = f.read()
		f.seek(0, 0)
		f.write(line.rstrip('\r\n') + '\n' + content)

def generatePOM(name):
	f = open("../pom-template.xml")
	newfiledata = []
	for line in f:
		newline = line
		if "<artifactId>ScoreData</artifactId>" in newline:
			newline = "<artifactId>"+name+"</artifactId>\n"
		newfiledata.append(newline)
	newfile = open("../pom.xml","w+")
	newfile.write("".join(newfiledata))
	newfile.close()

def writePackageInfo():
	print "Start writing package info"
	# add package ai.h2o... to top of all models in models dir
	for file in os.listdir(modelsdir):
		print "Writing package info to: " + file
		line_prepender(modelsdir+"/"+file, "package ai.h2o.hive.udf.models;")
		modelnames.append(file.split(".")[0])

	print "Finish writing package info"

def createModelsClass():
	print "Start writing static Models class"

	# create static Java class to know which classes to reflect in UDF
	f = open('../src/main/java/ai/h2o/hive/udf/Models.java', 'w+')
	f.write("package ai.h2o.hive.udf;\n\n")
	f.write("public class Models {\n")
	f.write("     public static final String[] NAMES = {\n")

	for x in range(len(modelnames)):
		f.write("          \"ai.h2o.hive.udf.models." + modelnames[x] + "\"")
		if x+1 == len(modelnames):
			f.write("};\n")
		else:
			f.write(",\n")

	f.write("};")
	f.close()
	print "Finish writing static Models class"

def printCompileCommand(name):
	print "BUILD COMMAND: Run the following command from the root directory of the project"
	print "mvn clean && mvn compile && mvn package -Dmaven.test.skip=true && java -cp ./localjars/h2o-genmodel.jar:./target/"+name+"-1.0-SNAPSHOT.jar ai.h2o.hive.udf.ScoreDataHQLGenerator"

def main():
	if len(sys.argv) == 1:
		raise Exception("Need 1 argument: name of output JAR")

	writePackageInfo()
	createModelsClass()

	name = sys.argv[1]
	generatePOM(name)
	printCompileCommand(name)

main()