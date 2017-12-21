class metamap{

class exec_script {
 exec { 'add_archi':
    command => 'dpkg --add-architecture i386 ',
    path    => '/usr/bin/:/usr/local/bin/:/bin/',
  }

	file{'deploy_metamap':
		ensure => 'file',
		path => '/home/inserm.dr.paris.5/scripts/deploy_metamap_proc.sh',
		owner => 'inserm.dr.paris.5',
		group => 'inserm.dr.paris.5',
		mode => '0755',
		notify => Exec['deploy_core']
		}

	file{'deploy_metamap_api':
		ensure => 'file',
		path => '/home/inserm.dr.paris.5/scripts/deploy_metamap_api.sh',
		owner => 'inserm.dr.paris.5',
		group => 'inserm.dr.paris.5',
		mode => '0755',
		notify => Exec['deploy_core_api']
		}
	file{'create_metamap_mark':
		ensure => 'file',
		path => '/home/inserm.dr.paris.5/scripts/create_metamap_mark.sh',
		owner => 'inserm.dr.paris.5',
		group => 'inserm.dr.paris.5',
		mode => '0755',
		notify => Exec['create_core_mark']
		}
}
                
class standard{
	
	$home_user_dir=hiera('user_dir')
	#class {exec_script:}
	include metamap::exec_script
	package {["bzip2","libc6-dev-i386"]:
		ensure => 'installed',
		require => Exec['add_archi']
        
	}
	exec { 'deploy_core':
               command => "/bin/bash -c '/home/$home_user_dir/scripts/deploy_metamap_proc.sh -o linux -y 2014'",
               #refreshonly => true,
               cwd => "/home/$home_user_dir",
               timeout     => 1800,
               before => Exec['deploy_core_api'],
	       unless => "/usr/bin/test -f '/home/$home_user_dir/.metamap_mark'"
        }
         exec { 'deploy_core_api':
               command => "/bin/bash -c '/home/$home_user_dir/scripts/deploy_metamap_api.sh -o linux -y 2014'",
               #refreshonly => true
               cwd => "/home/$home_user_dir",
               timeout     => 1800,
	       unless => "/usr/bin/test -f '/home/$home_user_dir/.metamap_mark'",
	       before => Exec['create_core_mark']
        }

        exec { 'create_core_mark':
                command => "/bin/bash -c '/home/$home_user_dir/scripts/create_metamap_mark.sh .metamap_mark'",
                #refreshonly => true
                cwd => "/home/$home_user_dir",
                timeout     => 1800,
		unless => "/usr/bin/test -f '/home/$home_user_dir/.metamap_mark'"
        }
}



class shared_resources {

	file{'/home/inserm.dr.paris.5/test':
                ensure => 'directory',
                path => '/home/inserm.dr.paris.5/scripts',
                owner  => 'inserm.dr.paris.5',
                group  => 'inserm.dr.paris.5',
                recurse => true,
                source => 'puppet:///modules/scripts'
        }
}
}
