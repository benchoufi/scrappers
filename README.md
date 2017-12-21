# scrappers

this repo consists of several tools dedicated to custom BIG DATA tools. 

## Deploying metamap on Google Cloud Storage

Following scripts are used to deploy METAMAP on clusters 

### tools

To instal and run `metamap`, you'll need to install the compression tool `bzip2`. On linux type machine, you'll run

  `apt-get install bzip2` 
  
The `metamap` running scripts relie on a 32bits script. Any 64bits computer that does not support 32bits will fail to launch `metamap`. For instance, on a linux type machine, you have to install the `GNU C` development libraries. 

  ```
  dpkg --add-architecture i386 
  apt-get install libc6:i386 // ubuntu distro
  ```
  
  ```
  dpkg --add-architecture i386 
  apt-get install libc6-dev-i386 // debian distro
  ```



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
  ./deploy_metamap.sh -o $OS -y $year` # -P is the prefix, here metamap and -b the name of your google cloud bucket, here metamap_hd
  
  this will deploy on all the whole cluser. If needed, you can deploy more strictly by specifying the -t (--target) flag, wihch must be 
  one of the following [master|workers|all].
  
  ** Remark **
  we are supposing you are using the `bdutil` google cloud deployment. If you are using the more recent `dataproc`, then you'll have to use the `deploy_metamap_proc.sh`

2. running `deploy_metamap_api.sh`
  
  you'll have to set the same flags are those of `deploy_metamap.sh`, so running 
  then you'll have to run 

 `./deploy_metamap_api.sh -o $OS -y $year`
 
3. starting and stopping servers `kick_metamap_servers.sh`
  
  you'll have to provide the year flag of the distribution : 
  -y, --year : stands for the 4 digits year of the package
  
  this script starts or stops the SKR/Medpost Tagger server, the Word Sense Disambiguation server, and the MM server. 

  to start servers, run 
  `sudo kick_metamap_servers.sh -y start`

  to stop servers, run
  `sudo kick_metamap_servers.sh -y stop` 
  
4. using puppet

User can use any favorite culster management tool, `Ansible`, `Chef` or whatever. Here, we make the choice of using `puppet`. User will have to install on the cluster, and then all the deployment routine occurs : so that `metamap` is intalled on all the nodes and, to the only condition the installation is correct, then the `metamap` servers are ran. 

To run the manifest, User has to go the /path/to/puppet/code/environments/production/manifests and then run `sudo puppet apply site.pp`, and all the magic happens ;) For example on a linux machine, this path is `/etc/puppetlabs/code/environments/production/manifests`

In the `modules` folder, one find the `scripts` that will be synced on all agents and the two main modules, namely `metamap` where all the metamap installation logic appears and `servers` well all the servers kick-off happens. We use `facter` to apply the main server commands. For now, to stop the server, User will have to 
- edit the `/path/to/puppet/code/environments/production/modules/servers/lib/facter/commands.rb` file and change `start` to `stop`
- run `export FACTER_order=stop`
- apply the  manifest again `sudo puppet apply site.pp` in `/path/to/puppet/code/environments/production/manifests`

We let in place some `test_*.pp` manfest file for debug purpose.

** Remark **
The usability of server "start and stop" commands need to be improved

### cluster synchronisation 

	Our main tool is `puppet`. First, you have got to install puppet on the master node of the cluster. Then add the manifest `site.pp` we provide in the manifet folder.
	In general, this folder is `/etc/puppetlabs/code/environments/production/manifests` - for a `debian` machine. When puppet is ran the necessary packages will be installed 
	all across the nodes, the `metamap` packages, that are already installed in the master node will be synced on the workers and the metamap servers will be kicked to start.
	 
	We provide also some custom base to do sync, for a handmade scripts. `rsync_nodes.sh` is intended to allow synchronization on some range of workers. This is not provided by `bdutils`. (this needs improvement to target nodes by name and range).


### filtering and metamap processing 

  [Clinical Trials](clinicaltrials.gov) is the database from which extracting the datasets.

  `filter_trials` deals with filtering each trial. 

  `metaprocess_trials` applies [UMLS ontology](www.nlm.nih.gov/research/umls/).
  the `metamap api` is called setting this options 
  
  By default, the options called are 

  ```
  -A -V USAbase -J acab,anab,comd,cgab,dsyn,emod,inpo,mobd,neop,patf,sosy
  ```
  
  To pass any other options, for example `-y  -Z 2014AB`, add this as the last argument of program arguments.  
  
  `avrosation_trials` deals combines filtering and metamap processing in two bound mapreduce jobs. the input format is `avro`, better suited for large number of small xml files.
  
  for the moment, there are two way of running `avrosation_trials` from the main method of `Process.java` or `ChainProcess.java`. the last one uses `hadoop` chaining. it must be more efficient. **need to be benchmarked** 
  
  **Remark** `avrosation_trials` contains 2 classes, namely `AvroReader` and `Avrowriter` that strictly supports transformation of the large number of xml files into one `Avro` container large file. this can be built and extracted as a separated jar.
  
  `spark-filter_trials` is the same trivial tool than `filter_trials`, except it runs with `spark`. To submit the `spark` job, you have to run 
  
  ```
  spark-submit --master yarn --class filter.ParseXML --packages com.databricks:spark-avro_2.10:1.0.0 $some_path/target/spark-filter-1.0-SNAPSHOT.jar $some_file_.avro some_output_dir
  ```
  we assume here that you added `spark-submit` to your `$PATH`
  
  **Remark**
  - First, the `$some_file_.avro` and `some_output_dir` are located in your distributed storage. 

  - Second, in some case, it might be necessary to add jars to this command, especially for `MetaMapApi`and `prologbeans` jar files. So that you should add the `--jars` flag to this command : 
  ```spark-submit --master yarn --class org.avrosation.filter.ParseXML --jars /path/to/MetaMapApi.jar,/path/to/prologbeans.jar --packages com.databricks:spark-avro_2.10:1.0.0 $some_path/target/spark-filter-1.0-SNAPSHOT.jar $some_file_.avro some_output_dir```
  
  `spark-metaprocess_trials` is the same trivial tool than `metaprocess_trials`, except it runs with `spark`. To submit, the command is
  ```
  spark-submit --master yarn —-class 
  metamap.MetaProcess —-jars $some_path/target/spark-metamap-process.jar $some_file_input.txt $some_file_input.txt output_files
  ```
  This command can be eventually completed by the following arguments `--num-executors number_of_executors --executor-cores number_of_cores` 
  
  - The follwing issue can occur after wrapping the java code into the `jar` file and then submit the command : 
  ```
  Exception in thread "main" java.lang.SecurityException: Invalid signature file digest for Manifest main attributes
  ```
   Then , we suggest the followong treatment of the `jar` file : `zip -d yourjar.jar 'META-INF/.SF' 'META-INF/.RSA' 'META-INF/*SF'
  
   
## BMJ scraping
in the src/ directory, you'll find the BMJ Open Access scrapper. 
