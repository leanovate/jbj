<?php
	/**
	* Links menu widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Links extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Links () {
			$this->plugin = 'Links';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_links');
		}
		
		function getContent () {
			$str = '';
			
			global $blog_config;
		
			if ( $GLOBALS[ 'logged_in' ] == true ) {
				// You are logged in.
				$str .= sprintf( '<b>%s<br />%s</b><br /><br />', $_SESSION[ 'user' ], _sb('notice_loggedin'));
				
				// There are x unmodded comments.
				if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') ) {
					$unmodCount = get_unmodded_count(true);
					if ( $unmodCount > 0 ) {
						if ( $blog_config->getTag('BLOG_COMMENTS_POPUP') == 1 ) {
							$width = $GLOBALS[ 'theme_vars' ][ 'popup_window' ][ 'width' ];
							$height = $GLOBALS[ 'theme_vars' ][ 'popup_window' ][ 'height' ];
							$str .= sprintf( '<a href="javascript:openpopup(\'comments_moderation.php\',%s,%s,true)">%s%d%s</a><br /><br />', $width, $height, _sb('notice_moderator1'), $unmodCount, _sb('notice_moderator2') );
						} else {
							$str .= sprintf( '<a href="comments_moderation.php">%s%d%s</a><br /><br />', _sb('notice_moderator1'), $unmodCount, _sb('notice_moderator2') );
						}
					}
				}
			}
			
			// Home.
			$str .= sprintf( '<a href="%sindex.php">%s</a><br />', BASEURL, _sb('menu_home') );
			
			// Contact.
			$temp = ($blog_config->getTag('BLOG_EMAIL'));
			if ( !empty( $temp ) ) {
				$oBlacklist = new CBlacklist;
				$oBlacklist->load( CONFIG_DIR . 'blacklist.txt' );
				if ( $oBlacklist->isBanned( getIP() ) == false || $GLOBALS[ 'logged_in' ] == true  ) {
					$str .= sprintf( '<a href="%scontact.php">%s</a><br />', BASEURL, _sb('menu_contact') );
				}
			}
			
			// Stats.
			if ( $blog_config->getTag('BLOG_ENABLE_STATS') ) {
				$str .= sprintf( '<a href="%sstats.php">%s</a><br />', BASEURL, _sb('menu_stats') );
			}
			
			// Read links file.
			$filename = CONFIG_DIR . 'links.txt';
			$result = fileio::read_file( $filename );
			
			// Build HTML.
			if ( $result ) {
				$array = explode('|', $result);
				for ( $i = 0; $i < count( $array ); $i = $i + 2 ) {
					$title = str_replace( '&amp;#124;', '|', $array[$i] );
					$url = str_replace( '&amp;#124;', '|', $array[$i+1] );
					if ( $url == '' ) { // This is a spacer
						$str .= '<br />' . $title . '<br />';
					} else {
/*						if ( strpos($url, 'http') === 0 ) { // Open in new window
							$str .= '<a href="' . $url . '" target="_blank">' . $title . '</a><br />';
						} else { // Open in same window*/
							$str .= '<a href="' . $url . '">' . $title . '</a><br />';
						/*}*/
					}
				}
			}
			
			// Login / Logout
			if ( $GLOBALS[ 'logged_in' ] == true ) {
				$str = $str . '<a href="add_link.php">[ ' . _sb('sb_add_link_btn')  . ' ]</a><br />';
				$str .= '<hr /><a href="logout.php">' . _sb('menu_logout') . '</a>';
			} else {
				if ( $blog_config->getTag('BLOG_ENABLE_LOGIN') ) {
					$str .= '<hr /><a href="login.php">' . _sb('menu_login') . '</a>';
				}
			}
			
			return $str;
		}
	}
	
	// ------------
	// Global Functions
	// ------------
	
	function load_links () {	
		// Read links file.
		$filename = CONFIG_DIR . 'links.txt';
		$str = fileio::read_file( $filename );
		
		$link_array = array();
		if ( $str ) {
			$array = explode('|', $str);
			for ( $i = 0; $i < count( $array ); $i = $i + 2 ) {
				$link_data = array();
				$link_data[ 'title' ] = str_replace( '&amp;#124;', '|', $array[$i] );
				$link_data[ 'url' ] = str_replace( '&amp;#124;', '|', $array[$i+1] );
				array_push( $link_array, $link_data);
			}
		}
		
		return $link_array;
	}
	
	function write_link ( $link_name, $link_url, $link_id ) {
		// Save new link. Update links file
		
		// Clean up link name and url and make safe for HTML and database storage.
		$link_name = clean_post_text( $link_name );
		$link_url = clean_post_text( $link_url );
		
		// Read old links file.
		$filename = CONFIG_DIR . 'links.txt';
		$str = fileio::read_file( $filename );
	
		// Append new links.
		if ( $str ) {
			$array = explode('|', $str);
			
			if ( $link_id !== '' ) {
				array_splice( $array, $link_id, 2 );
				array_splice( $array, $link_id, 0, array( $link_name, $link_url ) );
			} else {
				array_push( $array, $link_name );
				array_push( $array, $link_url );
			}
		} else {
			$array = array( $link_name, $link_url );
		}
		
		// Save links to file.
		$result = fileio::write_file( $filename, implode('|', $array) );
		
		if ( $result ) {
			return ( true );
		} else {
			// Error: Probably couldn't create file...
			return ( $filename );
		}
	}
	
	function modify_link ( $action, $link_id ) {
		// Modify links.
		// Move links up or down, edit or delete.
		
		// Read links file.
		$filename = CONFIG_DIR . 'links.txt';
		$str = fileio::read_file( $filename );
		
		// Append new links.
		if ( $str ) {
			$array = explode('|', $str);
			
			switch ( $action ) {
				case 'up':
					if ( count( $array ) > 2 && $link_id != 0 ) {
						$pop_array = array_splice( $array, $link_id, 2 );
						array_splice( $array, $link_id-2, 0, $pop_array );
					}
					break;
				case 'down':
					if ( count( $array ) > 2 && $link_id < ( count( $array ) - 3 ) ) {
						$pop_array = array_splice( $array, $link_id, 2 );
						array_splice( $array, $link_id+2, 0, $pop_array );
					}
					break;
				case 'delete':
					if ( $link_id <= ( count( $array ) - 1 ) ) {
						array_splice( $array, $link_id, 2 );
					}
					break;
				case 'delete_static':
					for ( $i = 0; $i < count( $array ); $i++ ) {
						if ( $link_id == $array[$i] ) {
							array_splice( $array, $i-1, 2 );
							break;
						}
					}
					break;
			}
		}
		
		// Save links to file.
		$result = fileio::write_file( $filename, implode('|', $array) );
		
		if ( $result ) {
			return ( true );
		} else {
			// Error: Probably couldn't create file...
			return ( $filename );
		}
	}
?>
