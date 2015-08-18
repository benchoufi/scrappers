# scrappers

this repo consists of several tools dedicated to custom BIG DATA tools. 

## Deploying metamap on Google Cloud Storage

Following scripts are used to deploy METAMAP on clusters 

### runnning the scripts 

1. providing correct flags to `deploy_metamap.sh`
  -o, --os : stands for the OS : it can be either linux, darwin (OSX)
  -y, --year : stands for the 4 digits year of the package
  -h, --help : stands for help (no information yet)

  the you'll have to run 
  `./deploy_metamap.sh -o $OS -y $year`

  - [x] **Windows** is not supported the same process needs to be done for **win32**

  after ensuring you have correctly installed the `bdutil` tool porvided by [Google Cloud](https://github.com/GoogleCloudPlatform/bdutil), you can run this 
 
 `./bdutil -P metamap -b metamap_hd -u deploy_metamap.sh run_command --\
  sudo -E env "PATH=$PATH" ./deploy_metamap.sh -o $OS -y $year`
  
  this will deploy on all the whole cluser. If needed, you can deploy more strictly by specifying the -t (--target) flag, wihch must be 
  one of the following [master|workers|all].

2. running `deploy_metamap_api.sh`
  
  you'll have to set the same flags are those of `deploy_metamap.sh`, so running 
  then you'll have to run 

 `./deploy_metamap_api.sh -o $OS -y $year`
 
3. starting and stopping servers `kick_metamap_servers.sh`
  
  you'll have to provide the year flag of the distribution : 
  -y, --year : stands for the 4 digits year of the package
  
  this script starts or stops the SKR/Medpost Tagger server, the Word Sense Disambiguation server, and the MM server. 

  to start servers, run 
  `kick_metamap_servers.sh -y start`

  to stop servers, run
  `kick_metamap_servers.sh -y stop`  

### custom synchronisation 

`rsync_nodes.sh` is intended to allow synchronization on some range of workers. This is not provided by `bdutils`. (this needs improvement to target nodes by name and range).

### filtering and metamap processing 

  [Clinical Trials](clinicaltrials.gov) is the database from which extracting the datasets.

  `filter_trials` deals with filtering each trial. 

  `metaprocess_trials` applies [UMLS ontology](www.nlm.nih.gov/research/umls/).
  the `metamap api` is called setting this options 
  
  By default, the options called are 

  ```
  "-A -V USAbase -J acab,anab,comd,cgab,dsyn,emod,inpo,mobd,neop,patf,sosy");
  ```
  
  To pass any other options, for example `-y  -Z 2014AB`, add this as the last argument of program arguments.  
  
  `avrosation_trials` deals combines filtering and metamap processing in two bound mapreduce jobs. the input format is `avro`, better suited for large number of small xml files.
  
  for the moment, there are two way of running `avrosation_trials` from the main method of `Process.java` or `ChainProcess.java`. the last one uses `hadoop` chaining. it must be more efficient. **need to be benchmarked** 
  
  **Remark** `avrosation_trials` contains 2 classes, namely `AvroReader` and `Avrowriter` that strictly supports transformation of the large number of xml files into one `Avro` container large file. this can be built and extracted as a separated jar.
  
  `spark-filter_trials` is the same trivial tool than `filter_trials`, except it runs with `spark`. It runs much faster. To submit the `spark` job, you have to run 
  
  ```
  ./bin/spark-submit --master yarn --class filter.ParseXML --packages com.databricks:spark-avro_2.10:1.0.0 $some_path/target/spark-filter-1.0-SNAPSHOT.jar $some_file_.avro some_output_dir
  ```
  `spark-metaprocess_trials` is the same trivial tool than `metaprocess_trials`, except it runs with `spark`. To submit, the command is the same than the former one except this time you don't need to package it with databricks' avro.
   
## BMJ scraping
in the src/ directory, you'll find the BMJ Open Access scrapper. 
