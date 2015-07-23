# scrappers

this repo consists of several tools dedicated to custom BIG DATA tools. 

## Deploying metamap on Google Cloud Storage

`deploy_metamap` script is used to deploy METAMAP on clusters 

### runnning the script 

1. providing correct flags
-o, --os : stands for the OS : it can be either linux, darwin (OSX)
-y, --year : stands for the 4 digits year of the package
-h, --help : stands for help (no information yet)

the you'll have to run 
`./deploy_metamap.sh -o $OS -y $year`

- [x] @benchoufi, **Windows** is not supported the same process needs to be done for **win32**

 After ensuring you have correctly installed the `bdutil` tool porvided by [Google Cloud](https://github.com/GoogleCloudPlatform/bdutil), you can run this 

 
 `./bdutil -P metamap -b metamap_hd -u deploy_metamap.sh run_command --\
  ./deploy_metamap.sh -o $OS -y $year`
  
  This will deploy on all the whole cluser. If needed, you can deploy more strictly by specifying the -t (--target) flag, wihch must be 
  one of the following [master|workers|all].

### custom synchronisation 

`rsync_nodes.sh` is intended to allow synchronization on some range of workers. This is not provided by `bdutils`. (this needs improvement to target nodes by name and range).

## BMJ scraping
in the src/ directory, you'll find the BMJ Open Access scrapper. 
