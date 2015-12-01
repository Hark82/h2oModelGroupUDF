### headers <- names(df)
library(h2o)
h2o.init("mr-0xd3", port = 54325)

# Get the header and column types
h <- "tmp/headers"
h1 <- read.table(file = h, header = F)
cols <- ifelse(trimws(h1$V2) == "string", "enum", "numeric")
heads <- trimws(h1$V1)

# Used particularly for this dataset tentatively
i = 17
cols <- cols[1:i]
heads <- heads[1:i]

# Import the data with proper column names and types
p <- "hdfs://mr-0xd6.0xdata.loc:8020/user/amy/adult_data"
d <- h2o.importFile(path = p, parse = F)
d1 <- h2o.parseRaw(data = d, col.types = cols,
                   destination_frame = "adult")
names(d1) <- heads

## Create a fake response column
rand <- h2o.runif(x = d1)
d1$fake <- ifelse(rand$rnd <= 0.5, "response.0", "response.1")

## Specify the response columns
myYs <- c("sex", "fake")
myX  <- setdiff(names(d1), myYs)

models <- c()
modelPath <- paste0(getwd(), "/tmp/generated_models") 
modelKeys <- paste0("gbm_", myYs)
for(y in myYs){
  gbm <- h2o.gbm(x = myX, y = y, training_frame = d1, model_id = paste0("gbm_", y))
  downloadJar <- ifelse(y == myYs[[1]], TRUE, FALSE )
  h2o.download_pojo(model = gbm, path = modelPath , getjar = downloadJar)
  models <- c(models, gbm)
}


