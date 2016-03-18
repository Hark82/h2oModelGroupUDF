import os

modelsdir = "../src/main/java/ai/h2o/hive/udf/models"
modelnames = []

# Add to the start of a file
def line_prepender(filename, line):
	with open(filename, 'r+') as f:
		content = f.read()
		f.seek(0, 0)
		f.write(line.rstrip('\r\n') + '\n' + content)

def main():
	# add package ai.h2o... to top of all models in models dir
	for file in os.listdir(modelsdir):
		line_prepender(modelsdir+"/"+file, "package ai.h2o.hive.udf.models;")
		modelnames.append(file.split(".")[0])

	# create static Java class to know which classes to reflect in UDF
	f = open('../src/main/java/ai/h2o/hive/udf/Models.java', 'w+')
	f.write("package ai.h2o.hive.udf;\n\n")
	f.write("public class Models {\n")
	f.write("     public static final String[] NAMES = {\n")

	for x in range(len(modelnames)):
		f.write("          \"ai.h2o.hive.udf.models." + modelnames[x] + "\"")
		if x+1 == len(modelnames):
			f.write("}\n")
		else:
			f.write(",\n")

	f.write("};")
	f.close()

main()