<?php

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com

	// -----------------
	// Utility Functions
	// -----------------

	function save_restore() {
        	$restore = restore_post();
	        if ($restore != NULL) {
        	        // do restore and reset here
                	$_POST = $restore[1];
	                reset_post();
        	}
       	        save_post();
	}


	function reset_post() {
		setcookie('lastposttype', '');
		setcookie('lastpost', '');
	}

	function save_post($type) {
		setcookie('lastposttype', $type);
		setcookie('lastpost', serialize($_POST));
	}

	function restore_post() {
		if (!empty($_COOKIE['lastpost'])) {
			return array($_COOKIE['lastposttype'], unserialize($_COOKIE['lastpost']));
		}
		return NULL;
	}

function return_bytes($val) {
    $val = trim($val);
    $last = strtolower($val[strlen($val)-1]);
    switch($last) {
        // The 'G' modifier is available since PHP 5.1.0
        case 'g':
            $val *= 1024;
        case 'm':
            $val *= 1024;
        case 'k':
            $val *= 1024;
    }

    return $val;
}

        function phpini_check() {
                // TODO translations
                // TODO more PHP checks here

		$dir = dirname(dirname(__file__));

                print "<p>PHP Configuration Checks:</p>";
                if (strnatcmp(phpversion(),'5.0') < 0)
                        echo "<p>ERROR: Your version of PHP (" . phpversion() . ") is too old.</p>";
                if (!open_basedir_check($dir . '/' . CONFIG_DIR))
                        echo "<p>ERROR: You need to set your php.ini <a href='http://www.php.net/manual/en/ini.core.php#ini.open-basedir'>open_basedir</a> to include $dir/" . CONFIG_DIR . ".</p>";
                if (!open_basedir_check($dir . '/' . CONTENT_DIR))
                        echo "<p>ERROR: You need to set your php.ini <a href='http://www.php.net/manual/en/ini.core.php#ini.open-basedir'>open_basedir</a> to include $dir/" . CONTENT_DIR . ".</p>";
                if (!open_basedir_check($dir . '/' . IMAGES_DIR))
                        echo "<p>ERROR: You need to set your php.ini <a href='http://www.php.net/manual/en/ini.core.php#ini.open-basedir'>open_basedir</a> to include $dir/" . IMAGES_DIR . ".</p>";
                //if (!ini_get('allow_url_fopen'))
                //        echo "<p>WARNING: php.ini config value allow_url_fopen is set to Off.  This is needed for trackbacks.</p>";
                if (!ini_get('file_uploads'))
                        echo "<p>WARNING: php.ini config value file_uploads is set to Off.  This is needed for uploading images and emoticons.</p>";
                if (intval(ini_get('max_file_uploads')) <= 0)
                        echo "<p>WARNING: php.ini config value max_file_uploads is set to zero or less.  This is needed for uploading images and emoticons.</p>";
                if (!function_exists('gzcompress'))
                        echo "<p>WARNING: Zlib module not present.  This is needed for compressing database files and web pages in transit.</p>";
                if (!function_exists('getimagesize'))
                        echo "<p>WARNING: GD library module not present.  This is needed for CAPCHA/anti-spam image support.</p>";
                echo "<p>NOTE: Maximum size for image uploads is " . min(intval(return_bytes(ini_get('upload_max_filesize'))), intval(return_bytes(ini_get('post_max_size'))))/(1024*1024) . " MB.  Adjust upload_max_filesize and post_max_size in php.ini to larger values if you need to upload bigger files.</p>";
        }


	function write_users($user_list) {
		new_write_users($user_list);
	}

	function read_users() {
		// check if old format file exists, if so, read and convert
		if (file_exists(CONFIG_DIR . "users.php")) {
			$user_list = old_read_users();
			new_write_users($user_list);
			// delete users.php here
			unlink(CONFIG_DIR . "users.php");
			return $user_list;
		}
		return new_read_users();
	}

        function old_write_users($user_list) {
                $newfile = '';
                foreach ($user_list as $user) {
                        $str = implode('|', $user);
                        $newfile .= htmlspecialchars(trim($str)) . "\n";
                }

                // Now post the new file with the updated information
                $pfile = fopen(CONFIG_DIR."users.php","w");
                fwrite($pfile, trim($newfile));
                fclose($pfile);
        }


        function new_write_users($user_list) {
                $newfile = '';
                foreach ($user_list as $user) {
                        $str = implode('|', $user);
                        $newfile .= htmlspecialchars(trim($str)) . "\n";
                }

                // Now post the new file with the updated information
                $pfile = fopen(CONFIG_DIR."users2.php","w");
                fwrite($pfile, "<?php \$users = \"" . base64_encode(trim($newfile)) . '"; ?>');
                fclose($pfile);
        }

        function new_read_users() {
                $users = '';
                @include(CONFIG_DIR."users2.php");
                $user_list = array();

		if (empty($users)) {
			return $user_list;
		}
                $userstr = base64_decode($users);
                $users = explode("\n", $userstr);

                foreach ($users as $line) {
                        $tmp = explode('|', trim($line));
                       	$user_list[] = $tmp;
                }

                return $user_list;
        }

        function old_read_users() {
                        $user_list = array();
                        $pfile = @fopen(CONFIG_DIR."users.php","r");
                        if ($pfile === FALSE) {
                                return $user_list;
                        }
                        while (!feof($pfile)) {
                                $line = fgets($pfile);
                                $tmp = explode('|', trim($line));
                                $user_list[] = $tmp;
                        }
                        fclose($pfile);
                return $user_list;
        }

function sb_curPageURL() {
 return sb_host() . $_SERVER["SCRIPT_NAME"];
}

function sb_host() {
 $pageURL = 'http';
 if ($_SERVER["HTTPS"] == "on") {$pageURL .= "s";}
 $pageURL .= "://";
 if ($_SERVER["HTTPS"] == "on" && $_SERVER["SERVER_PORT"] != "443") {
  $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"];
 } elseif ($_SERVER["HTTPS"] != "on" && $_SERVER["SERVER_PORT"] != "80") {
  $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"];
 } else {
  $pageURL .= $_SERVER["SERVER_NAME"];
 }
 return $pageURL;
}
	
	function open_basedir_check($dir) {
		$basedir = ini_get('open_basedir');
		if (empty($basedir))
			return true;

		$root = dirname(dirname(__FILE__));
		$baselist = explode(PATH_SEPARATOR, $basedir);
		foreach ($baselist as $base) {
			if (strstr($dir, $base) !== FALSE) {
				return true;
			}
		}
		return false;
	}

	/**
	* Return the microtime.
	*
	* @return		float
	*/
	function microtime_float() { 
		return( microtime( true ) );
	}
	

	// Activate PHP's GZ compression output, if not currently activated.
	// Must be called before any header output.
	function sb_gzoutput ()
	{
		// Contributed by: Javier Gutierrez, guti <at> ya <dot> com
		//
	  	if ( ( ini_get( 'zlib.output_compression' ) != '0' ) && ( ini_get('zlib.output_compression' ) != 'On' ) && ( extension_loaded('zlib') ) )
		{
			ini_set( 'zlib.output_compression_level', 9);
			ob_start( 'ob_gzhandler' );
			ini_restore( 'zlib.output_compression_level' );
		}
	}

	// Support function for upgrading to / downgrading from trackback enabled version
	//
	// (All versions are now trackback enabled. So, we need to move all the comments... -- Alex)
	function move_all_comment_files( $is_upgrade = true, $dont_move_files = false ) {
		// Use the "$dont_move_files" flag to check if any files need to be moved but
		// don't actually move them. This is used on the "login_cgi.php" page.
		$basedir = CONTENT_DIR;
		$count = 0;

		$dir = $basedir;
		if ( is_dir( $dir ) ) {
			if ( $year_dir_handle = @opendir( $dir ) ) {
				while ( ( $year_dir = readdir( $year_dir_handle ) ) !== false ) {
					if ( is_dir( $dir.$year_dir ) ) {
						if ( $year_dir != '.' && $year_dir != '..' ) {
							if ( $year_dir != 'static' ) {

								// MONTH directories

								if ( $month_dir_handle = @opendir( $dir.$year_dir . '/' ) ) {
									while ( ( $month_dir = readdir( $month_dir_handle ) ) !== false ) {
										if ( is_dir( $dir.$year_dir.'/'.$month_dir ) ) {
											if ( $month_dir != '.' && $month_dir != '..' ) {

												// ENTRIES

												if ( $entry_dir_handle = @opendir( $dir.$year_dir.'/'.$month_dir . '/' ) ) {
													while ( ( $entry_filename = readdir( $entry_dir_handle ) ) !== false ) {
														if ( ! is_file( $dir.$year_dir.'/'.$month_dir.'/'.$entry_filename ) ) {

															if( $is_upgrade ) {
																// move comment* to  comment/ subdir
																$comments_dir = $entry_filename;
															} else {
																// move comment/* to .
																$comments_dir = $entry_filename.'/comments';
															}

															if ( is_dir( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir ) ) {
																if ( $comments_dir != '.' && $comments_dir != '..' ) {
																	if ( $comments_dir_handle = @opendir( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/' ) ) {
																		while ( ( $comment_filename = readdir( $comments_dir_handle ) ) !== false ) {
																			if ( ( is_file( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/'.$comment_filename ) ) && ( strpos($comment_filename, 'comment') !== false ) ) {
																				if( $is_upgrade ) {

																					// Check that comments/ dir exists
																					if (!file_exists( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/comments' )) {
																						$oldumask = umask(0);
																						$ok = mkdir( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/comments', BLOG_MASK );
																						umask($oldumask);
																					}

																					if ( $dont_move_files == false ) {
																						echo $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/'.$comment_filename."<br />";
																						rename( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/'.$comment_filename, $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/comments/'.$comment_filename);
																					}
																					$count++;
																				} else {
																					if ( $dont_move_files == false ) {
																						echo $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/'.$comment_filename."<br />";
																						rename( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/'.$comment_filename, $dir.$year_dir.'/'.$month_dir.'/'.$entry_filename.'/'.$comment_filename);

																						// Can we clean up the comments/ subdir?
																						$file_array = sb_folder_listing( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir.'/', array( '.txt', '.gz' ) );
																						if ( count( $file_array ) == 0 ) {
																							sb_delete_directory( $dir.$year_dir.'/'.$month_dir.'/'.$comments_dir );
																						}
																					}
																					$count++;
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return( $count );
	}

	// Support function for downgrading from trackback enabled version
	function delete_all_trackbacks() {
		$basedir = CONTENT_DIR;
		$count = 0;

		$dir = $basedir;
		if ( is_dir( $dir ) ) {
			if ( $year_dir_handle = @opendir( $dir ) ) {
				while ( ( $year_dir = readdir( $year_dir_handle ) ) !== false ) {
					if ( is_dir( $dir . $year_dir ) ) {
						if ( $year_dir != '.' && $year_dir != '..' ) {
							if ( $year_dir != 'static' ) {

								// MONTH directories

								if ( $month_dir_handle = @opendir( $dir.$year_dir . '/' ) ) {
									while ( ( $month_dir = readdir( $month_dir_handle ) ) !== false ) {
										if ( is_dir( $dir.$year_dir.'/'.$month_dir ) ) {
											if ( $month_dir != '.' && $month_dir != '..' ) {

												// ENTRIES

												if ( $entry_dir_handle = @opendir( $dir.$year_dir.'/'.$month_dir . '/' ) ) {
													while ( ( $entry_filename = readdir( $entry_dir_handle ) ) !== false ) {
														if ( ! is_file( $dir.$year_dir.'/'.$month_dir.'/'.$entry_filename ) ) {

															$trackbacks_dir = $entry_filename.'/trackbacks';

															if ( is_dir( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir ) ) {
																if ( $trackbacks_dir != '.' && $trackbacks_dir != '..' ) {
																	if ( $trackbacks_dir_handle = @opendir( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir.'/' ) ) {
																		while ( ( $trackback_filename = readdir( $trackbacks_dir_handle ) ) !== false ) {
																			if ( ( is_file( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir.'/'.$trackback_filename ) ) && ( strpos($trackback_filename, 'trackback') !== false ) ) {
																				echo $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir.'/'.$trackback_filename.'<br />';
																				sb_delete_file( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir.'/'.$trackback_filename );
																				$count++;

																				// Can we clean up the trackbacks/ subdir?
																				$file_array = sb_folder_listing( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir.'/', array( '.txt', '.gz' ) );
																				if ( count( $file_array ) == 0 ) {
																					sb_delete_directory( $dir.$year_dir.'/'.$month_dir.'/'.$trackbacks_dir );
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return( $count );
	}

	function sb_get_capcha () {
		$capcha=rand(100000, 999999);
		return( $capcha );
	}

	function assign_rand_value($num)
	{
	// accepts 1 - 36
  switch($num)
  {
    case "1":
     $rand_value = "a";
    break;
    case "2":
     $rand_value = "b";
    break;
    case "3":
     $rand_value = "c";
    break;
    case "4":
     $rand_value = "d";
    break;
    case "5":
     $rand_value = "e";
    break;
    case "6":
     $rand_value = "f";
    break;
    case "7":
     $rand_value = "g";
    break;
    case "8":
     $rand_value = "h";
    break;
    case "9":
     $rand_value = "i";
    break;
    case "10":
     $rand_value = "j";
    break;
    case "11":
     $rand_value = "k";
    break;
    case "12":
     $rand_value = "l";
    break;
    case "13":
     $rand_value = "m";
    break;
    case "14":
     $rand_value = "n";
    break;
    case "15":
     $rand_value = "o";
    break;
    case "16":
     $rand_value = "p";
    break;
    case "17":
     $rand_value = "q";
    break;
    case "18":
     $rand_value = "r";
    break;
    case "19":
     $rand_value = "s";
    break;
    case "20":
     $rand_value = "t";
    break;
    case "21":
     $rand_value = "u";
    break;
    case "22":
     $rand_value = "v";
    break;
    case "23":
     $rand_value = "w";
    break;
    case "24":
     $rand_value = "x";
    break;
    case "25":
     $rand_value = "y";
    break;
    case "26":
     $rand_value = "z";
    break;
    case "27":
     $rand_value = "0";
    break;
    case "28":
     $rand_value = "1";
    break;
    case "29":
     $rand_value = "2";
    break;
    case "30":
     $rand_value = "3";
    break;
    case "31":
     $rand_value = "4";
    break;
    case "32":
     $rand_value = "5";
    break;
    case "33":
     $rand_value = "6";
    break;
    case "34":
     $rand_value = "7";
    break;
    case "35":
     $rand_value = "8";
    break;
    case "36":
     $rand_value = "9";
    break;
  	}
	return $rand_value;
	}

	function get_rand_id($length)
	{
  	if($length>0)
  	{
  	$rand_id="";
   	for($i=1; $i<=$length; $i++)
   	{
   	mt_srand((double)microtime() * 1000000);
   	$num = mt_rand(1,36);
   	$rand_id .= assign_rand_value($num);
   	}
  	}
	return $rand_id;
	}

?>
