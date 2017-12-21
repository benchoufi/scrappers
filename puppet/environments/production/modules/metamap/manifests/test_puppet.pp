class test_resources {
        user { 'default_test':
                              ensure           => 'present',
                              home             => '/home/default_test',
                              comment           => 'Judy default_test',
                              groups            => 'video',
                              password         => '!!',
                              password_max_age => '99999',
                              password_min_age => '0',
                                shell            => '/bin/bash',
                              shell            => '/bin/bash',
                             uid              => '501',
                           }

        file{'/home/inserm.dr.paris.5/test':
                ensure => 'directory',
                path => '/home/inserm.dr.paris.5/test',
                recurse => true,
                source => '/home/inserm.dr.paris.5/test'
        }
}
