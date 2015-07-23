# scrappers

this repo consists of several tools dedicated to custom BIG DATA tools. 

## Deploying metamap on Google Cloud Storage

deploy_metamap script is used to deploy METAMAP on clusters 

### runnning the script 

1. providing correct flags
-o, --os : stands for the OS : it can be either linux, darwin (OSX)
-y, --year : stands for the 4 digits year of the package
-h, --help : stands for help (no information yet)

the you'll have to run 
`./deploy_metamap.sh -o $OS -y $year`

### deploying on google cloud
 After ensuring you have correctly installed the `bdutil` tool porvided by Google Cloud, you can run this 
 
 `./bdutil -P metamap -b metamap_hd -u deploy_metamap.sh run_command -- ./deploy_metamap.sh -o $OS -y $year`

## BMJ scraping
in the src/ directory, you'll find the BMJ Open Access scrapper. 
