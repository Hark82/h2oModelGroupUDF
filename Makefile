build:
	mkdir tmp
	mkdir tmp/generated_models
	mkdir localjars
	./scripts/grab_headers.sh
	R -f GBM-example.R
	mv tmp/generated_models/h2o-genmodel.jar localjars
	cp -r tmp/generated_models/* src/main/java/ai/h2o/hive/udf/
clean:
	rm -rf tmp
	rm -rf localjars
run:
	clean
	build
