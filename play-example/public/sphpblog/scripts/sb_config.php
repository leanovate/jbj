<?php
	require_once("config.php");
	require_once("classes/configuration.php");

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com

	// read_config ( )
	// write_colors ( $post_array, $user_file )
	// read_colors ( )

	// ----------------
	// Config Functions
	// ----------------

	function read_config ( ) {
		// Read config information from file.
		//
		global $blog_config;
		
		// I've got a minor issue here. You have to set the language before you
		// can call strings.php. The strings file doesn't get called until after
		// read_config. So... I'm just putting in some english values here....
		
		$blog_config = new Configuration();
		$blog_config->read_file();
		
		/*
		if ( !isset( $blog_config->getTag('COMMENT_TAGS_ALLOWED') ) ) {
			$blog_config->setTag('COMMENT_TAGS_ALLOWED') = explode(',', 'b,i,strong,em,url');
		} else {
			$blog_config->setTag('COMMENT_TAGS_ALLOWED') = explode(',', $blog_config->getTag('COMMENT_TAGS_ALLOWED'));
		}
		*/
		
		// LOAD THEME
		global $blog_theme;
		$blog_theme = $blog_config->getTag('BLOG_THEME');
		require_once(ROOT_DIR . '/themes/' . $GLOBALS['blog_config']->getTag('BLOG_THEME') . '/themes.php');

		// LOAD COLORS
		read_colors();

		// Start GZIP Output
		if ( $blog_config->getTag('BLOG_ENABLE_GZIP_OUTPUT') ) {
			sb_gzoutput();
		}
	}

	// ----------------
	// Blacklist
	// ----------------

	function add_to_blacklist( $new_address ) {
		$old_address_list = $GLOBALS['blog_config']->getTag('BANNED_ADDRESS_LIST');
		
		$new_address_list = trim($new_address.chr(13).$old_address_list);
		
		$GLOBALS['blog_config']->setTag('BANNED_ADDRESS_LIST', $new_address_list);
		
		$result = $GLOBALS['blog_config']->write_file();
		
		return $result;
	}
	
	function get_installed_translations() {
		$dir = ROOT_DIR.'languages/';	
		$translation_arr = array();;
		clearstatcache();
		
		if ( is_dir($dir) ) {
			$dhandle = opendir($dir);
			if ( $dhandle ) {
				$sub_dir = readdir( $dhandle );
				while ( $sub_dir ) {
					if ( is_dir( $dir . $sub_dir ) == true && $sub_dir != '.' && $sub_dir != '..' ) {
						$lang_dir = $sub_dir;
						$lang_name = sb_read_file( $dir . $sub_dir . '/id.txt' );
						if ( $lang_name ) {
							$lang_arr = array();
							$lang_arr['directory'] = $lang_dir;
							$lang_arr['name'] = $lang_name;
							
							array_push( $translation_arr, $lang_arr );
						}
					}
					$sub_dir = readdir( $dhandle );
				}
			}
			closedir( $dhandle );
		}
		
		return( $translation_arr );
	}
	
	function validate_language($temp_lang) {
		$translation_arr = get_installed_translations();
		$ok = false;
		for ($i=0; $i < count($translation_arr); $i++) {
			if ( $temp_lang === $translation_arr[$i]['directory'] ) {
				$ok = true;
				break;
			}
		}
		return( $ok );
	}

	// ----------------------
	// Color Config Functions
	// ----------------------

	function write_colors ( $post_array, $user_file ) {
		// Save color information to file.
		//
		global $blog_theme;

		$str = implode('|', $post_array);

		if ( isset( $user_file ) ) {
			if (!file_exists(CONFIG_DIR.'schemes')) {
				$oldumask = umask(0);
				$ok = mkdir(CONFIG_DIR.'schemes', BLOG_MASK );
				umask($oldumask);
			}
			$custom_file = CONFIG_DIR.'schemes/' . $user_file . '.txt';
			$result = sb_write_file( $custom_file, $str );
		}

		$filename = CONFIG_DIR.'colors-' . $blog_theme . '.txt';
		$result = sb_write_file( $filename, $str );

		if ( $result ) {
			return ( true );
		} else {
			// Error:
			// Probably couldn't create file...
			return ( $filename );
		}
	}

	function read_colors ( ) {
		// Read color information from file.
		//
		global $user_colors, $blog_theme;
		$color_def = theme_default_colors();
		for ( $i = 0; $i < count( $color_def ); $i++ ) {
			$user_colors[ $color_def[$i][ 'id' ] ] = $color_def[$i][ 'default' ];
		}

		$filename = CONFIG_DIR.'colors-' . $blog_theme . '.txt';
		$result = sb_read_file( $filename );
		if ( $result ) {
			$saved_colors = explode('|', $result);
			for ( $i = 0; $i < count( $saved_colors ); $i = $i + 2 ) {
				$id = $saved_colors[$i];
				$color = $saved_colors[$i+1];
				$user_colors[ $id ] = $color;
			}
		}
	}

	function get_block_list () {
		// Create the right-hand block. Return array
		//global $blog_content, $blog_subject, $blog_text, $blog_date, $user_colors, $logged_in;
		//global $lang_string;

		// Read blocks file.
		$filename = CONFIG_DIR.'blocks.txt';
		$result = sb_read_file( $filename );

		$blocklist = array();
		if ( $result ) {
			$blocklist = explode('|', $result);
			for ( $i = 0; $i < count( $blocklist ); $i+=2 ) {
				$blocklist[ 'title' ] = blog_to_html( $blocklist[$i], false, false, false, true );
				$blocklist[ 'text' ] = blog_to_html( $blocklist[$i+1], false, false, false, true );
			}
		}
		return ( $blocklist );
	}
?>
