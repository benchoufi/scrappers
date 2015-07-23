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

- [x] @benchoufi, **Windows** is not supported the same process needs to be done for **win32**

 after ensuring you have correctly installed the `bdutil` tool porvided by [Google Cloud](https://github.com/GoogleCloudPlatform/bdutil), you can run this 

 
 `./bdutil -P metamap -b metamap_hd -u deploy_metamap.sh run_command --\
  ./deploy_metamap.sh -o $OS -y $year`
  
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

## BMJ scraping
in the src/ directory, you'll find the BMJ Open Access scrapper. 
