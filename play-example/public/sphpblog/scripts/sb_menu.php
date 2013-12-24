<?php

  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes 
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

  // ------------------------
  // "Archive Menu" Functions
  // ------------------------

  /**************************************************************************
  MODIFICACIONES PARA LA GESTION DE LOS BLOQUES FIJOS DEL SPHPBLOG
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  * Se ha modificado la funcion "read_blocks" para que diferencie entre los
    bloques por defecto (#) y los definidos por el usuario, y en el caso de
    los primeros llame a la funcion correspondiente. (Lineas 419 a 427)
  **************************************************************************/
  
   // Sverd1 March 17, 2006
  function dateString() {
    $dateArray = read_dateFormat();
    $dateToday = explode("/", $dateArray[ 'sDate_order' ]);
    foreach($dateToday as $dToday) {
      if ($dToday == 'Day') {
        $dateString[] = '%d';
      } elseif ($dToday == 'Month' || $dToday == 'MMM') {
        $dateString[] = '%m';
      } elseif ($dToday == 'Year') {
        $dateString[] = '%y';
      }
    }
    if (empty($dateString)) {
        return "";
    }
    return ( implode("/", $dateString) );
  }
  
  function read_menus_tree ( $m, $y, $d, $max_chars=75, $base_url='index.php', $showall=false ) {
    // Create the right-hand navigation menu and Archives page. Return HTML
    // hint: $d isn't used for anything anymore
    $entry_array = blog_entry_listing();
    // $entry_array[$i] = implode( '|', array( $entry_filename, $year_dir, $month_dir ) ) );
    
    $str = '';
    if ( count( $entry_array ) > 0 ) {
      $str_year = '';
      $str_month = '';
      $str_day = '';
      
      list( $last_filename, $last_y, $last_m ) = explode( '|', $entry_array[ 0 ] );
      $last_d = substr($last_filename, 9, 2);
      
      $str = '';
      $str.= '<div id="archive_tree_menu"><ul>';
      
      for ( $n = 0; $n <= count( $entry_array ) - 0; $n++ ) {
      
        if ( $n == count( $entry_array ) ) {
          list( $curr_filename, $curr_y, $curr_m ) = explode( '|', $entry_array[ $n-1 ] );
        } else {
          list( $curr_filename, $curr_y, $curr_m ) = explode( '|', $entry_array[ $n ] );
        }
        $curr_d = substr($curr_filename, 9, 2);
        
        // Month
        if ( $last_m != $curr_m || $last_y != $curr_y || $n == count( $entry_array ) ) {
          
          // Build Month List
          $str_month .= '<li>' . "\n";
          $temp_str = ( strftime( '%B', mktime(0, 0, 0, $last_m, $last_d, $last_y ) ) );
          $str_month .= '<a href="' . $base_url . '?m=' . $last_m . '&amp;y=' . $last_y . '">' . $temp_str . '</a>' . "\n";
          
          // Fixed per Sverd1 March 17, 2006
          if (!empty($str_day)) {
            $str_month .= '<ul>' . "\n" . $str_day . "\n" . '</ul>' . "\n";
          }
          
          $str_month .= '</li>' . "\n";
          
          $str_day = '';
          $last_m = $curr_m;
        }
        
        // Year
        if ( $last_y != $curr_y || $n == count( $entry_array ) ) {
        
          // Build Year List
          $temp_str = ( strftime( '%Y', mktime(0, 0, 0, $last_m, $last_d, $last_y ) ) );
          $str_year .= '<li>' . "\n";
          $str_year .= $temp_str . "\n";
          $str_year .= '<ul>' . "\n";
          $str_year .= $str_month . "\n";
          $str_year .= '</ul>' . "\n";
          $str_year .= '</li>' . "\n";
          $str .= $str_year;
          
          $str_year = '';
          $str_month = '';
          $str_day = '';
          
          $last_y = $curr_y;
        }
        
        // Day
        if ( $curr_y == $y && $curr_m == $m || $showall == true ) {
          
          // Build Day List
          $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $curr_y . '/' . $curr_m . '/' . $curr_filename );
          
          $curr_array = Array();
          $curr_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, true );
          // Fixed per Sverd1 March 17, 2006
          $curr_array[ 'date' ] = ( strftime( dateString(), mktime(0, 0, 0, $curr_m, $curr_d, $curr_y ) ) );
          $curr_array[ 'entry' ] = blog_to_html( $blog_entry_data[ 'CONTENT' ], false, true );

          $str_day .= '<li>' . "\n";
          $str_day .= '<a href="index.php?m=' . $curr_m . '&amp;y=' . $curr_y . '&amp;entry=' . sb_strip_extension( $curr_filename ) . '">' . $curr_array[ 'subject' ] . '</a><br />' . "\n";
          $str_day .= '<b>' . $curr_array[ 'date' ] . '</b>';
          if ( $max_chars == 0) {
            // Don't show any of the entry...
          } else if ( strlen( $curr_array[ 'entry' ] ) > $max_chars ) {
            // Truncate...
            $str_day .= "<br />\n";
            $str_day .= substr( $curr_array[ 'entry' ], 0, $max_chars) . "<p />\n";
          } else {
            $str_day .= "<br />\n";
            $str_day .= $curr_array[ 'entry' ] . "<p />\n";
          }
          $str_day .= '</li>' . "\n";
        }
        
      }
      
      $str .= '</ul></div>';
    }       
    return( $str );
  }

  // ----------------------
  // "Links Menu" Functions
  // ----------------------
  
  function read_links ( $logged_in ) {
    // Create the right-hand link menu. Return HTML
    //
  
    // Read links file.
    $filename = CONFIG_DIR.'links.txt';
    $result = sb_read_file( $filename );
    
    // Append new links.
    $str = NULL;
    if ( $result ) {
      $array = explode('|', $result);
      for ( $i = 0; $i < count( $array ); $i = $i + 2 ) {
        if ( $array[$i+1] == '' ) {
          $str  .= '<br />' . $array[$i] . '<br />';
        } else {
          if ( strpos($array[$i+1], 'http') === 0 ) {
            $str  .= '<a href="' . $array[$i+1] . '" target="_blank">' . $array[$i] . '</a><br />';
          } else {
            $str  .= '<a href="' . $array[$i+1] . '">' . $array[$i] . '</a><br />';
          }
        }
      }
    }

    // Show invisible links when logged in.
    if ( $logged_in == true ) {
      $dir = CONTENT_DIR.'static/';
      $contents = sb_folder_listing( $dir, array( '.txt','.gz' ) );
      for ( $i = 0; $i < count( $contents ); $i++ ) {
        $staticfile = sb_read_file( $dir . $contents[ $i ] );
        $exploded_array = explode( '|', $staticfile );
        $blog_entry_data = explode_with_keys( $exploded_array );
        if ( $blog_entry_data[ 'MENU_VISIBLE' ] == false ) {
          $str .= '<a href="static.php?page=' . sb_strip_extension( $contents[ $i ] ) . '">*' . $blog_entry_data[ 'SUBJECT' ] . '</a><br />';
        }
      }
    }

    if ( $logged_in == true ) {
      $str  .= '<a href="add_link.php">[ ' . _sb('sb_add_link_btn')  . ' ]</a><br />';
    }



    return ( $str );
  }

function get_blocks() {
    //default order
    $plugin_array = array('Avatar', 'Links', 'AuthoringMenu', 'Preferences', 'Calendar', 'RandomEntry', 'Archives', 'Categories', 'Search', 'CounterTotals', 'RecentEntries', 'RecentComments');

    // Read blocks file.
    $filename = CONFIG_DIR.'blocks.txt';
    $result = sb_read_file( $filename );

    $arrlist = array();
    if ( $result ) {
      $arrlist = array_merge($arrlist, explode('|', $result));
    }

    //add on plugins that aren't in the list
    $plugins = scandir(ROOT_DIR . '/scripts/plugins/sidebar/');
    foreach ($plugins as $plugin) {
        if (is_dir('scripts/plugins/sidebar/' . $plugin) && $plugin[0] != '.') {
            if (array_search($plugin, $plugin_array) === FALSE) {
                $plugin_array[] = $plugin;
            }
        }
    }

    $add = array();
    foreach ($plugin_array as $plugin) {
        $plugin_obj = new $plugin;
        $result = array_search($plugin, $arrlist);
        if ($result === FALSE && $plugin_obj->getEnabled()) {
            $add[] = $plugin;
            $add[] = 'plugin';
        }
        elseif($result !== FALSE && !$plugin_obj->getEnabled()) {
        // Remove disabled plugins here
            array_splice($arrlist, $result, 2);
        }
    }

    return array_merge($arrlist, $add);
}
  
  // ----------------------------
  // "Blocks" Functions
  // ----------------------------

  function read_blocks ( $logged_in ) {
    // Create the right-hand block. Return array
    //

    global $blog_content, $blog_subject, $blog_text, $blog_date, $user_colors, $logged_in, $blog_config;
    global $lang_string;

    // Append new blocks.
    $block_array = array();
      $array = get_blocks();
      for ( $i = 0; $i < count( $array ); $i+=2 ) {
        // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
        if ($array[$i + 1] == 'plugin') {
          // handle as a plugin
            if (class_exists( $array[$i])) {
                        $plugin = new $array[$i];
			$val = $plugin->display();
			$block_array[$i] = $val['title'];
                        $block_array[$i+1] = $val['content'];
                        unset( $plugin );
            }
        }
        elseif ( (($blog_config->getTag('BLOG_ENABLE_STATIC_BLOCK') == true) and ( $array[$i] != $blog_config->getTag('STATIC_BLOCK_OPTIONS') ))
           or ($blog_config->getTag('BLOG_ENABLE_STATIC_BLOCK') == false) ) {
          $block_array[$i] = blog_to_html( $array[$i], false, false, false, true );
          $block_array[$i + 1] = blog_to_html( $array[$i + 1], false, false, false, true );
        }
      }

    return ( $block_array );
  }
  
  function write_block ( $block_name, $block_content, $block_id ) {
    // Save new block. Update blocks file
    //
    
    // Clean up block name and make safe for HTML and text database storage.
    global $lang_string;
    $block_name = str_replace( '|', ':', $block_name );
    $block_name = htmlspecialchars( $block_name, ENT_QUOTES, $lang_string[ 'php_charset' ] );

    // Clean up block url and make safe text database storage.
    $block_content = clean_post_text(str_replace( '|', ':', $block_content ));

    // Read old blocks file.
    $filename = CONFIG_DIR.'blocks.txt';
    $result = sb_read_file( $filename );

    // Append new blocks.
    if ( $result ) {
      $array = explode('|', $result);
      
      if ( $block_id !== '' ) {
        array_splice( $array, $block_id, 2 );
        array_splice( $array, $block_id, 0, array( $block_name, $block_content ) );
      } else {
        array_push( $array, $block_name );
        array_push( $array, $block_content );
      }
    } else {
      $array = array( $block_name, $block_content );
    }
    
    // Save blocks to file.
    $str = implode('|', $array);
    $result = sb_write_file( $filename, $str );
    
    if ( $result ) {
      return ( true );
    } else {
      // Error:
      // Probably couldn't create file...
      return ( $filename );
    }
  }
  
  function modify_block ( $action, $block_id ) {
    // Modify blocks.
    // Move blocks up or down, edit or delete.
    
    // Read blocks file.
    $filename = CONFIG_DIR.'blocks.txt';
    //$result = sb_read_file( $filename );
    
    // Append new blocks.
//    if ( $result ) {
  //    $array = explode('|', $result);
      $array = get_blocks();
      
      if ( $action === 'up' ) {
        if ( count( $array ) > 2 && $block_id != 0 ) {
          $pop_array = array_splice( $array, $block_id, 2 );
          array_splice( $array, $block_id-2, 0, $pop_array );
        }
      }
      if ( $action === 'down' ) {
        if ( count( $array ) > 2 && $block_id < ( count( $array ) - 3 ) ) {
          $pop_array = array_splice( $array, $block_id, 2 );
          array_splice( $array, $block_id+2, 0, $pop_array );
        }
      }
      if ( $action === 'delete' ) {
        if ( $block_id <= ( count( $array ) - 1 ) ) {
          array_splice( $array, $block_id, 2 );
        }
      }
      if ( $action === 'delete_static' ) {
        for ( $i = 0; $i < count( $array ); $i++ ) {
          if ( $block_id == $array[$i] ) {
            array_splice( $array, $i-1, 2 );
            break;
          }
        }
      }
   // }
    
    // Save blocks to file.
    $str = implode('|', $array);
    $result = sb_write_file( $filename, $str );
    
    if ( $result ) {
      return ( true );
    } else {
      // Error:
      // Probably couldn't create file...
      return ( $filename );
    }
  }
  // DATOH_END

  // ----------------------------
  // "Most Recent Menu" Functions
  // ----------------------------

  function confirm_unmod( $modflag ) {
    global $blog_config;
    $result = true;
    if ($blog_config->getTag('BLOG_COMMENTS_MODERATION') == 1) {
      if ( $modflag == 'H' ) { $result = false; }
    }

    return( $result );
  }

  function add_most_recent_trackback ( $trackback_id, $y, $m, $blog_entry_id ) {
    global $blog_config;
    
    // Add an item to the 'Last Updated' List
    //
    
    // Read links file.
    $filename = CONFIG_DIR.'last_updated_trackback.txt';
    $result = sb_read_file( $filename );
    
    // Append new links.
    if ( $result ) {
      $array = explode('|', $result);
      array_push( $array, $blog_entry_id, $m, $y, $trackback_id );
    } else {
      $array = array( $blog_entry_id, $m, $y, $trackback_id );
    }
    
    $max_comments = $blog_config->getTag('BLOG_MAX_ENTRIES');
    if ( count( $array ) > ( ( $max_comments * 4 ) - 1 ) ) {
      // $array = array_reverse( $array );
      $array = array_slice( $array, $max_comments * -4, $max_comments * 4);
      // $array = array_reverse( $array );
    }
    
    // Save links to file.
    $str = implode( '|', $array );
    sb_write_file( $filename, $str );
  }
  
  function delete_most_recent_trackback ( $item_filename ) {
    // Delete an item to the 'Last Updated' List
    //
    
    // Read links file.
    $filename = CONFIG_DIR.'last_updated_trackback.txt';
    $result = sb_read_file( $filename );

    $blog_entry_id = str_replace( '/', '', sb_strip_extension( strrchr( $item_filename, '/') ) );
    
    // Append new links.
    $str = NULL;
    $update_file = false;
    if ( $result ) {
      $array = explode('|', $result);
      $array = array_reverse( $array );
      for ( $i = 0; $i < count( $array ); $i = $i + 4 ) {
        if ( $blog_entry_id == $array[$i] ) {
          array_splice( $array, $i, 4 );
          $update_file = true;
        }
      }
    }
    
    // Save links to file.
    if ( $update_file ) {
      $array = array_reverse( $array );
      $str = implode('|', $array);
      sb_write_file( $filename, $str );
    }
  }
  
  function get_most_recent_trackback () {
    // Read last updated items from disk, return HTML
    //
    global $lang_string, $user_colors;
    
    // Read links file.
    $filename = CONFIG_DIR.'last_updated_trackback.txt';
    $result = sb_read_file( $filename );
    
    // Append new links.
    $str_trackbacks = NULL;
    if ( $result ) {
      $array = explode('|', $result);
      $array = array_reverse( $array );
      for ( $i = 0; $i < count( $array ); $i = $i + 4 ) {
        $trackback_id = $array[$i+0];
        $y = $array[$i+1];
        $m = $array[$i+2];
        $blog_entry_id = $array[$i+3];
        
        $trackback_file = CONTENT_DIR.$y.'/'.$m.'/'. sb_strip_extension( $blog_entry_id ).'/trackbacks/'.$trackback_id;
        if ( file_exists( $trackback_file . '.txt' ) ) {
          $trackback_file  .= '.txt';
        } elseif ( file_exists( $trackback_file . '.txt.gz' ) ) {
          $trackback_file  .= '.txt.gz';
        }
        
        $trackback_entry_data = comment_to_array( $trackback_file );
        if ( $trackback_entry_data !== false) {
          $trackback_date = $trackback_entry_data[ 'DATE' ];
          $trackback_title = $trackback_entry_data[ 'TITLE' ];
          $trackback_blogname = $trackback_entry_data[ 'BLOGNAME' ];
          
          if ( strlen( $trackback_title ) > 40 ) {
            $trackback_title = substr( $trackback_title, 0, 40 );
            $trackback_title = substr( $trackback_title, 0, strrpos( $trackback_title, ' ' ) ) . '...';
          }
          
          if ( strlen( $trackback_blogname ) > 40 ) {
            $trackback_blogname = substr( $trackback_blogname, 0, 40 );
            $trackback_blogname = substr( $trackback_blogname, 0, strrpos( $trackback_blogname, ' ' ) ) . '...';
          }
          
          global $blog_config, $theme_vars;
          $str_trackbacks  .= '<a href="trackback.php?y='.$y.'&amp;m='.$m.'&amp;entry='.$blog_entry_id.'&amp;__mode=html">'.$trackback_title.'</a><br />';
          
          // $str_trackbacks = $str_trackbacks . format_date_menu( $trackback_date ) . '<br />';
          $str_trackbacks  .= format_date( $trackback_date ) . '<br />';
          $str_trackbacks  .= $trackback_blogname . '<p />';
        }
      }
    }
    
    return ( $str_trackbacks );
  }
?>
