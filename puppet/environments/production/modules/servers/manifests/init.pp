class servers {



class kickers {
	$home_user_dir=hiera('user_dir')
	#$param_1="/home/inserm.dr.paris.5/scripts/kick_metamap_servers.sh -y 2014 $order"
	
	file{'kick_servers':
        	ensure => 'file',
        	path => '/home/inserm.dr.paris.5/scripts/kick_metamap_servers.sh',
        	owner => 'inserm.dr.paris.5',
        	group => 'inserm.dr.paris.5',
        	mode => '0755',
       	        notify => Exec['kick_metamap_servers']
        }

	exec { 'kick_metamap_servers':
               command => "/bin/bash -c '/home/inserm.dr.paris.5/scripts/kick_metamap_servers.sh -y 2014 ${::order}'",
               #refreshonly => true,
               cwd => "/home/$home_user_dir",
               timeout     => 1800
        }
}

}

#node default {
#    include server
#}
