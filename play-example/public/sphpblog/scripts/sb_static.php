<?php

  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes 
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

  // ------------
  // Static Entry
  // ------------

  function static_entry_to_array ( $entryFile ) {
    // Reads a blog entry and returns an key/value pair array.
    //
    // Returns false on fail...
    global $sb_info;
    $blog_entry_data = array();
        
    $str = sb_read_file( $entryFile );
    $exploded_array = explode( '|', $str );
    
    if ( count( $exploded_array ) > 1 ) {
      if ( count( $exploded_array ) <= 6 ) {
        // Old List Format: subject, date, content      
        $blog_entry_data[ 'VERSION' ]  = $sb_info[ 'version' ];
        $blog_entry_data[ 'SUBJECT' ]  = $exploded_array[0];
        $blog_entry_data[ 'DATE' ]     = $exploded_array[1];
        $blog_entry_data[ 'CONTENT' ]  = $exploded_array[2];
        
      } else {
        // New Format: key/value pairs
        // VERSION, SUBJECT, DATE, CONTENT
        
        $blog_entry_data = explode_with_keys( $exploded_array );
      }
      return( $blog_entry_data );
    } else {
      // Exploded array only contained 1 item, so something is wrong...
      return( false );
    }
  }
  
  function read_static_entry ( $entry_id, $logged_in ) {
    // Read entries by month and year. Generate HTML output.
    //
    // Used for the main Index page.
    global $blog_config, $user_colors;
    
    $dir = CONTENT_DIR.'static/';
    
    if ( file_exists( CONTENT_DIR.'static/' . $entry_id . '.txt' ) ) {
      $filename = CONTENT_DIR.'static/' . $entry_id . '.txt';
    } else if ( file_exists( CONTENT_DIR.'static/' . $entry_id . '.txt.gz' ) ) {
      $filename = CONTENT_DIR.'static/' . $entry_id . '.txt.gz';
    } else if ( file_exists( CONTENT_DIR.'static/_404.txt.gz' ) ) {
      $filename = CONTENT_DIR.'static/_404.txt.gz';
    } else if ( file_exists( CONTENT_DIR.'static/_404.txt' ) ) {
      $filename = CONTENT_DIR.'static/_404.txt';
    } else {
      redirect_to_url( 'index.php' );
      return;
    }
    
    $blog_entry_data = static_entry_to_array( $filename );
        
    // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
    $entry_array = array();
    $entry_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, false, false, true );
    $entry_array[ 'date' ] = blog_to_html( format_date( $blog_entry_data[ 'DATE' ] ), false, false );
    $entry_array[ 'entry' ] = blog_to_html( $blog_entry_data[ 'CONTENT' ], false, false, false, true ) . '<br />';
    $entry_array[ 'menu_visible' ] = $blog_entry_data[ 'MENU_VISIBLE' ];
    $entry_array[ 'logged_in' ] = $logged_in;
    
    // Author edit and delete
    $entry = sb_strip_extension( $entry_id );
    if ( $logged_in == true ) {
      $entry_array[ 'edit' ][ 'name' ] = _sb('sb_edit');
      $entry_array[ 'edit' ][ 'url' ] = 'add_static.php?entry='.$entry;
      $entry_array[ 'delete' ][ 'name' ] = _sb('sb_delete');
      $entry_array[ 'delete' ][ 'url' ] = 'delete_static.php?entry='.$entry;
    }
    
    return ( $entry_array );
  }

  function write_static_entry ( $blog_subject, $blog_text, $updateFile, $file_name, $blog_menu_visible=true  ) {
    // Save new static entry or update old static entry
    //
    global $blog_config, $sb_info;
    
    $save_data = array();
    $save_data[ 'VERSION' ] = $sb_info[ 'version' ];
    $save_data[ 'SUBJECT' ] = clean_post_text( $blog_subject );
    $save_data[ 'CONTENT' ] = clean_post_text( $blog_text );
    $save_data[ 'MENU_VISIBLE' ] = $blog_menu_visible;

    if ( $updateFile != NULL ) {
      // Updating a static entry
      //
      // We need to grab the date of the old entry
      if ( file_exists( CONTENT_DIR.'static/' . $updateFile . '.txt' ) ) {
        $oldEntryFile = CONTENT_DIR.'static/' . $updateFile . '.txt';
      } elseif ( file_exists( CONTENT_DIR.'static/' . $updateFile . '.txt.gz' ) ) {
        $oldEntryFile = CONTENT_DIR.'static/' . $updateFile . '.txt.gz';
      }
      
      $oldEntryArray = static_entry_to_array( $oldEntryFile );
      $save_data[ 'DATE' ] = $oldEntryArray[ 'DATE' ];
      
      sb_delete_file( CONTENT_DIR.'static/' . $updateFile . '.txt.gz' );
      sb_delete_file( CONTENT_DIR.'static/' . $updateFile . '.txt' );

      $entryFile = $file_name . '.txt';
      
      // (We're just going to assume that the files and dirs exist...)
    } else {
      // Create a static entry
      //
      // This is going to be a new static entry.
      // We might need to create some directories.
      //
      // The directory and file structure is:
      // 'content/static/entryYYMMDD-HHMMSS.txt'
      $blog_date = time();
      $save_data[ 'DATE' ] = $blog_date;

      if (!file_exists(CONTENT_DIR)) {
        $oldumask = umask(0);
        $ok = mkdir(CONTENT_DIR, BLOG_MASK );
        umask($oldumask);
      }
      
      if (!file_exists(CONTENT_DIR.'static')) {
        $oldumask = umask(0);
        $ok = mkdir(CONTENT_DIR.'static', BLOG_MASK );
        umask($oldumask);
        if (!$ok) {
          return ( 'Couldn\'t creating directory: '.CONTENT_DIR.'static' );
        }
      }

      $stamp = date('ymd-His');
      $entryFile = $file_name.'.txt';

    }

    // Implode the array
    $str = implode_with_keys( $save_data );
    
    // Save the file
    $result = sb_write_file( CONTENT_DIR.'static/'.$entryFile, $str );

    if ( $save_data[ 'MENU_VISIBLE' ] != 'on' ) {
      modify_link( 'delete_static', 'static.php?page='.sb_strip_extension( $entryFile ), '' );
      return ( true );
    } else if ( $result ) {
      if ( $updateFile == NULL ) {
        // Add link to menu
        write_link( $save_data[ 'SUBJECT' ], 'static.php?page='.sb_strip_extension( $entryFile ), '' );
      } else {
        // Rename link
        $link_url = 'static.php?page='.sb_strip_extension( $updateFile );
        $result = sb_read_file( CONFIG_DIR.'links.txt' );

        // Append new links.
        if ( $result ) {
          $array = explode('|', $result);
          for ( $i = 0; $i < count( $array ); $i++ ) {
            if ( $link_url == $array[$i] ) {
              write_link( $save_data[ 'SUBJECT' ], 'static.php?page='.sb_strip_extension( $entryFile ), $i-1 );
              return ( true );
            }
          }
          // Was not in the file for some reason - add it
          write_link( $save_data[ 'SUBJECT' ], 'static.php?page='.sb_strip_extension( $entryFile ), $i );
        } else {
          // The file was empty because all of the links were invisible!
          write_link( $save_data[ 'SUBJECT' ], 'static.php?page='.sb_strip_extension( $entryFile ), 0 );
        }
      }
      return ( true );
    } else {
      // Error:
      // Probably couldn't create file...
      return ( CONTENT_DIR.'static/'.$entryFile );
    }
  }
  
  function get_static_entry_by_file( $entry_id ) {
    // Read an entry from disk and create the HTML
    //
    global $logged_in;

    if ( file_exists( CONTENT_DIR.'static/' . $entry_id . '.txt' ) ) {
      $filename = CONTENT_DIR.'static/' . $entry_id . '.txt';
    } elseif ( file_exists( CONTENT_DIR.'static/' . $entry_id . '.txt.gz' ) ) {
      $filename = CONTENT_DIR.'static/' . $entry_id . '.txt.gz';
    }

    $blog_entry_data = static_entry_to_array( $filename );
        
    // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
    $entry_array = array();
    $entry_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, false, false, true );
    $entry_array[ 'date' ] = blog_to_html( format_date( $blog_entry_data[ 'DATE' ] ), false, false );
    $entry_array[ 'entry' ] = blog_to_html( $blog_entry_data[ 'CONTENT' ], false, false, false, true ) . '<br />';
    $entry_array[ 'menu_visible' ] = $blog_entry_data[ 'MENU_VISIBLE' ];

    $blog_content = theme_staticentry( $entry_array, $logged_in );
    
    return ( $blog_content );
  }
?>
