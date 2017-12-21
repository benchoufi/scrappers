node default {
	#include metamap::exec_script
	#include metamap::standard
#class { 'metamap::exec_script': }
#class { 'metamap::standard':}
class { 'servers::kickers':
 # require => Class['metamap::standard'],
}
}
