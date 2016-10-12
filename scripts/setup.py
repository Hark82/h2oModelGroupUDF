import os
import sys

modelsdir = "../src/main/java/ai/h2o/hive/udf/models"
modelnames = []

# Add to the start of a file
def line_prepender(filename, line):
	with open(filename, 'r+') as f:
		content = f.read()
		if content[0:7] == "package":
			print "skipping: %s -- package definition exists" % filename
			return
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

	print "Finish writing package info"

def createModelsClass():
	print "Start writing static Models class"

	# create static Java class to know which classes to reflect in UDF
	f = open('../src/main/java/ai/h2o/hive/udf/Models.java', 'w+')
	f.write("package ai.h2o.hive.udf;\n\n")
	f.write("public class Models {\n")
	f.write("     public static final String[] NAMES = {\n")

	# if supplied, get modelorder
	if len(sys.argv) == 3:
		m = open(sys.argv[2], 'r')
		modelorder = []
		for line in m.readlines():
			modelorder.append(line.strip())
		m.close()

	# load models in supplied order or not
	mdir = os.listdir(modelsdir)
	if len(sys.argv) == 3:
		for model in modelorder:
			for line in mdir:
				if model == line:
					modelnames.append(line.split(".")[0])
	else:
		for line in mdir:
			modelnames.append(line.split(".")[0])

	# print to Models.java
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
	if len(sys.argv) < 2:
		raise Exception("Missing arguments! Usage: setup.py outputJarName [modelorder]")

	writePackageInfo()
	createModelsClass()

	name = sys.argv[1]
	generatePOM(name)
	printCompileCommand(name)

main()