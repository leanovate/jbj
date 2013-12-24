<?php

  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

  // read_entries ( $m, $y, $d, $logged_in, $start_entry, $category )
  // get_latest_entry ()
  // blog_entry_listing ()
  // entry_exists ( $y, $m, $entry )                 
  // preview_entry ( $blog_subject, $blog_text, $tb_ping, $temp_relatedlink, $timestamp )
  // preview_static_entry ( $blog_subject, $blog_text )
  // read_entry_from_file ( $entry_id )

  // ----------------------
  // Blog Display Functions
  // ----------------------
  function in_arrayr($needle, $haystack) {
    if ( is_array($haystack) ) {
      // haystack is array
      foreach ($haystack as $value) {
        if (is_array($needle)) {
          // needle is array
          foreach ($needle as $needle_val) {
            $result = in_arrayr($needle_val, $value);
            if ( $result ) {
              return true;
            }
          }
        } else if (is_array($value)) {
          // value is array
          $result = in_arrayr($needle, $value);
          if ( $result ) {
            return true;
          }
        } elseif ($needle == $value) {
          return true;
        } else {
          return false;
        }
       }
    } else {
      // haystack is not array
      if (is_array($needle)) {
        // needle is array
        foreach ($needle as $needle_val) {
          $result = in_arrayr($needle_val, $haystack);
          if ( $result ) {
            return true;
          }
        }
      } else {
        // needle is not array
        if ( $needle == $haystack ) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  function read_entries ( $m, $y, $d, $logged_in, $start_entry, $category, $is_permalink=false ) {
    // Read entries by month, year and/or day. Generate HTML output.
    //
    // Used for the main Index page.
    global $lang_string, $blog_config, $user_colors, $theme_vars;

    $entry_file_array = blog_entry_listing();

    // Loop through the $entry_file_array looking for the
    // first match. Note that $d could be NULL in which
    // case we get the first entry for the month.
    //
    // This is fine because this is actually what we
    // are looking for anyway.
    //
    // I'm just using a brute force method, I'm sure there
    // are better ways to do this... :)
    if ( $start_entry != NULL ) {
      $look_for = str_replace(' ', '-', $start_entry);
    } else {
      // 'dummy' entry name...
      $look_for = 'entry' . $y . $m . $d;
    }

    $entry_index = 0;
    for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
      if ( stristr(str_replace(' ', '-', $entry_file_array[ $i ]), $look_for) !== FALSE) {
        // MATCH!
        $entry_index = $i;
        break;
      }
    }

    $blog_max_entries = $blog_config->getTag('BLOG_MAX_ENTRIES');
    if ($is_permalink) {
      $blog_max_entries = 1;
    }

    // Grab the next X number of entries
    $file_array = array();
    if ( isset( $category ) ) {
      // Filter Entries by Category
      //
      // Unfortunately we actually have to open up the file
      // and read it to figure out what category the entry
      // belongs to. I think we should probably start saving
      // an index file of all the entries and categories.
      // I'm sure it would be faster when blogs start to have
      // to 1000's of entries.
      //

      $cats = explode( ',', $category);
      $cat_sub_arr = array();
      for ($i = 0; $i < count($cats); $i++)
      {
        $subcats = get_sub_categories($cats[$i]);
        for ($j = 0; $j < count($subcats); $j++)
          array_push($cat_sub_arr, $subcats[$j]);
        array_push($cat_sub_arr, $cats[$i]);
      }

      for ( $i = $entry_index; $i < count( $entry_file_array ); $i++ ) {
        list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
        $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
        if ( array_key_exists( 'CATEGORIES', $blog_entry_data ) ) {
          $cat_array = explode( ',', $blog_entry_data[ 'CATEGORIES' ] );

          if ( in_arrayr( $cat_array, $cat_sub_arr ) ) {
            array_push( $file_array, $entry_file_array[ $i ] );
            // Look for +1 entries (for the next button...)
            if ( count( $file_array ) >= $blog_max_entries + 1 ) {
              break;
            }
          }
          /*
          for ( $j=0; $j < count($cat_array); $j++ ) {
            if ( $cat_array[ $j ] == $category ) {
              array_push( $file_array, $entry_file_array[ $i ] );
              // Look for +1 entries (for the next button...)
              if ( count( $file_array ) >= $blog_max_entries + 1 ) {
                // We've found all X entries.
                // Break out of the "j" and the "i" loops.
                break 2;
              } else {
                // We've added this entry to the list,
                // we don't want to accidently add the
                // entry again. (This is mainly here
                // for future expansion if we start
                // doing searches for multiple categories
                // at the same time...)
                break 1;
              }
            }
          }
          */
        }
      }


      // new bit to get list of entries in current category (NEW CAT NUMBERS)
      $cat_file_array = array();
      if(isset($category)){
          $cat_cat_sub_arr = get_sub_categories($category);
          array_push( $cat_cat_sub_arr, $category );
          for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
              list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
              $cat_blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
              if ( array_key_exists( 'CATEGORIES', $cat_blog_entry_data ) ) {
                  $cat_cat_array = explode( ',', $cat_blog_entry_data[ 'CATEGORIES' ] );
                  if ( in_arrayr( $cat_cat_array, $cat_sub_arr ) ) {
                      array_push( $cat_file_array, $entry_file_array[ $i ] );
                  }
              }
          }
      }
      //determines which entry is currently being viewd in a category (NEW CAT NUMBERS)
      $cat_entry_index = 0;
      for ( $i = 0; $i < count( $cat_file_array ); $i++ ) {
          if ( $look_for == substr( $cat_file_array[ $i ], 0, strlen( $look_for ) ) ) {
              // MATCH!
              $cat_entry_index = $i;
              break;
          }
      }

      // Store info for next and previous links...
      if ( count( $file_array ) > $blog_max_entries ) {
        $next_entry = array_pop( $file_array );
      } else {
        $next_entry = NULL;
      }

      // Now we have to search backwards...
      if ( $entry_index == 0 ) {
        $previous_entry = NULL;
      } else {
        $previous_file_array = array();
        for ( $i = $entry_index; $i >= 0; $i-- ) {
          list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
          $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
          if ( array_key_exists( 'CATEGORIES', $blog_entry_data ) ) {
            $cat_array = explode( ',', $blog_entry_data[ 'CATEGORIES' ] );
            for ( $j=0; $j < count($cat_array); $j++ ) {
              if ( $cat_array[ $j ] == $category ) {
                array_push( $previous_file_array, $entry_file_array[ $i ] );
                // Look for +1 entries (for the next button...)
                if ( count( $previous_file_array ) >= $blog_max_entries + 1) {
                  // We've found all X entries.
                  // Break out of the "j" and the "i" loops.
                  break 2;
                } else {
                  // We've added this entry to the list,
                  // we don't want to accidently add the
                  // entry again. (This is mainly here
                  // for future expansion if we start
                  // doing searches for multiple categories
                  // at the same time...)
                  break 1;
                }
              }
            }
          }
        }

        $previous_entry = $previous_file_array[ count( $previous_file_array ) - 1 ];

        list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $previous_entry );
        $entry = sb_strip_extension( $entry_filename );

        // Previous entry and current start entry are the same.
        if ( $entry == $start_entry ) {
          $previous_entry = NULL;
        }
      }
    } else {
      // No Filtering
      for ( $i = $entry_index; $i < min( ( $blog_max_entries + $entry_index ), count( $entry_file_array ) ); $i++ ) {
        array_push( $file_array, $entry_file_array[ $i ] );
      }

      // Store info for next and previous links...
      if ( $entry_index + $blog_max_entries < count( $entry_file_array ) ) {
        $next_entry = $entry_file_array[ $entry_index + $blog_max_entries ];
      } else {
        $next_entry = NULL;
      }

      $previous_entry = NULL;
      if ( $entry_index > 0 ) {
        if ( $entry_index - $blog_max_entries > 0 ) {
          $previous_entry = $entry_file_array[ $entry_index - $blog_max_entries ];
        } else {
          $previous_entry = $entry_file_array[ 0 ];
        }
      }
    }

    // Read entry files
    $contents = array();
    for ( $i = 0; $i < count( $file_array ); $i++ ) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $file_array[ $i ] );
      array_push( $contents, array(   'entry' => $entry_filename,
                      'year' => $year_dir,
                      'month' => $month_dir ) );
    }

    // Flip entry order
    if ( $blog_config->getTag('BLOG_ENTRY_ORDER') == 'old_to_new' ) {
      $contents = array_reverse( $contents );
    }

    $blog_content = '';
    if ( $contents ) {
      $base_permalink_url = BASEURL;

      // I'm putting this check in here for people who have made
      // custom themes before I added these values...
      global $theme_vars;
      if ( is_array( $theme_vars ) ) {
        if ( isset( $theme_vars[ 'popup_window' ][ 'width' ] ) === false ) {
          $theme_vars[ 'popup_window' ][ 'width' ] = 500;
        }
        if ( isset( $theme_vars[ 'popup_window' ][ 'height' ] ) === false ) {
          $theme_vars[ 'popup_window' ][ 'height' ] = 500;
        }
        if ( isset( $theme_vars[ 'options'][ 'disallow_colors' ] ) === false ) {
          $theme_vars[ 'options'][ 'disallow_colors' ] = 0;
        }
      } else {
        $theme_vars = array();
        $theme_vars[ 'popup_window' ][ 'width' ] = 500;
        $theme_vars[ 'popup_window' ][ 'height' ] = 500;
        $theme_vars[ 'options'][ 'disallow_colors' ] = 0;
      }

      for ( $i = 0; $i <= count( $contents ) - 1; $i++ ) {
        // Read and Parse Blog Entry
        $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $contents[$i][ 'year' ] . '/' . $contents[$i][ 'month' ] . '/' . $contents[$i][ 'entry' ] );

        $entry_array = array();

        // Subject / Date
        // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
        $entry_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, false, false, true );
        $entry_array[ 'date' ] = blog_to_html( format_date( $blog_entry_data[ 'DATE' ] ), false, false );
        $entry_array[ 'date_numeric_day' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'NUMDAY' ), false, false );
        $entry_array[ 'date_numeric_month' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'NUMMONTH' ), false, false );
        $entry_array[ 'date_numeric_year' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'NUMYEAR' ), false, false );
        $entry_array[ 'date_alpha_month' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'ALPHAMONTH' ), false, false );
        $entry_array[ 'date_numeric_time' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'TIMENORMAL' ), false, false );
        $entry_array[ 'date_suffix_day' ] = blog_to_html( format_date_class( $blog_entry_data[ 'DATE' ],'SUFFIXDAY' ), false, false );

        // Categories
        if ( array_key_exists( 'CATEGORIES', $blog_entry_data ) ) {
          $temp_cat_array = explode( ',', $blog_entry_data[ 'CATEGORIES' ] );
          $temp_cat_names = Array();
          for ( $j = 0; $j < count( $temp_cat_array ); $j++ ) {
            array_push( $temp_cat_names, get_category_by_id ( $temp_cat_array[$j] ) );
          }
          $entry_array[ 'categories' ] = $temp_cat_names;
          $entry_array[ 'categories_id'] = $temp_cat_array;
        }

        // Read More link
        if ( array_key_exists( 'relatedlink', $blog_entry_data ) ) {
        $entry_array[ 'relatedlink' ][ 'name' ] = _sb('sb_relatedlink');
        $entry_array[ 'relatedlink' ][ 'url' ] = $blog_entry_data[ 'relatedlink' ];
        }

        // Author edit and delete
        $entry = sb_strip_extension( $contents[$i][ 'entry' ] );
        $y = sb_strip_extension( $contents[$i][ 'year' ] );
        $m = sb_strip_extension( $contents[$i][ 'month' ] );
        //$blog_entry_data[ 'CREATEDBY' ]
        $admin = "no";
        if (!empty($_SESSION[ 'fulladmin' ]))
            $admin = $_SESSION[ 'fulladmin' ];
        if ( (( $logged_in == true ) and ( $admin == 'yes' )) or
           (( $logged_in == true) and ( $admin == 'no' ) and ( CheckUserSecurity( $_SESSION[ 'username' ], 'EDIT' ) == true ) and ( $blog_entry_data[ 'CREATEDBY' ] != $_SESSION[ 'username' ]) ) or
           (( $logged_in == true) and ( $admin == 'no' ) and ( $blog_entry_data[ 'CREATEDBY' ] == $_SESSION[ 'username' ]) ))
        {
          $entry_array[ 'edit' ][ 'name' ] = _sb('sb_edit');
          $entry_array[ 'edit' ][ 'url' ] = 'add.php?y='.$y.'&amp;m='.$m.'&amp;entry='.$entry;
        }

        if ( (( $logged_in == true ) and ( $admin == 'yes' )) or
           (( $logged_in == true) and ( $admin == 'no' ) and ( CheckUserSecurity( $_SESSION[ 'username' ], 'DEL' ) == true ) ))
        {
          $entry_array[ 'delete' ][ 'name' ] = _sb('sb_delete');
          $entry_array[ 'delete' ][ 'url' ] = 'delete.php?y='.$y.'&amp;m='.$m.'&amp;entry='.$entry;
        }

        $entry_array[ 'permalink' ][ 'name' ] = _sb('sb_permalink');
        $entry_array[ 'permalink' ][ 'url' ] = $base_permalink_url . 'index.php?entry=' . str_replace(' ', '-', $blog_entry_data[ 'SUBJECT' ]);

        // // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
        $entry_array[ 'entry' ] = blog_to_html( $blog_entry_data[ 'CONTENT' ], false, false, false, true ) . '<br />';

        // Comments link and count
        $comment_trackback_base = CONTENT_DIR.$y.'/'.$m.'/'.$entry.'/';
        $comment_path = $comment_trackback_base.'comments/';
        $comment_array = sb_folder_listing( $comment_path, array( '.txt', '.gz' ) );

        // This is not a real count if some of the items haven't been modded yet...
        if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == true ) {
          if ( $logged_in == true ) {
            $comment_count = count( $comment_array );
          } else if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') != true ) {
            $comment_count = count( $comment_array );
          } else {
            // Cycle through the comments if there are some and find out how many are modded
            if ( count( $comment_array ) != 0) {
              $comment_count = count( $comment_array ) - get_entry_unmodded_count($y, $m, $entry);
            } else {
              $comment_count = 0;
            }
          }
        } else {
          $comment_count = 0;
        }

        // Trackbacks link and count
        $trackback_path = $comment_trackback_base.'trackbacks/';
        $trackback_array = sb_folder_listing( $trackback_path, array( '.txt', '.gz' ) );
        $trackback_count = count( $trackback_array );

        // Read view counter file
        $view_counter = 0;
        $view_array = sb_folder_listing( $comment_trackback_base, array( '.txt' ) );
        if ( in_array( 'view_counter.txt', $view_array ) ) {
          $view_counter = intval( sb_read_file( $comment_trackback_base . 'view_counter.txt' ) );
        }

        // Entry Rating
        if ( $blog_config->getTag('BLOG_ENABLE_VOTING') == true ) {
          $rating_array = read_rating( $y, $m, $entry );
          if ( $rating_array ) {
            $points = $rating_array[ 'points' ];
            $votes = $rating_array[ 'votes' ];
            $rating = $points / $votes / 5;
          } else {
            $points = 0;
            $votes = 0;
            $rating = 0;
          }

          global $blog_theme;
          $str = '';
          for ( $star_number = 1; $star_number <= 5; $star_number++ ) {
            $temp_ratio = ( $star_number / 5 );
            if ( $rating >= ( $temp_ratio - .2 ) && $rating < ( $temp_ratio - .1 ) ) {
              $star_image = 'no_star.png';
            } else if ( $rating >= ( $temp_ratio - .1 ) && $rating < $temp_ratio ) {
              $star_image = 'half_star.png';
            } else if ( $rating >= $temp_ratio ) {
              $star_image = 'full_star.png';
            } else {
              $star_image = 'no_star.png';
            }
            $str  .= '<a rel="nofollow" href="rate_cgi.php?y=' . $y . '&amp;m=' . $m . '&amp;entry=' . $entry . '&amp;rating=' . $star_number . '" title="' . _sb('sb_rate_entry_btn') . '"><img height="9" width="9" src="themes/' . $blog_theme . '/images/stars/' . $star_image . '" alt="$star_image" /></a>';
          }
          $entry_array[ 'stars_nototals' ] = $str;
          $str  .= ' ( ' . round( $rating * 5, 1 ) . ' / ' . $votes . ' )';
          $entry_array[ 'stars' ] = $str;
        }

        // Has to be populated regardless - used by the more tag
        $entry_array[ 'comment' ][ 'url' ] = 'comments.php?y='.$y.'&amp;m='.$m.'&amp;entry='.$entry;

        // Comments / Read - will show regardless of comments being enabled
        if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == true ) {
          $commenttext = _sb('sb_comment_btn');
          $commentplural = _sb('sb_comments_plural_btn');
          $comment = _sb('sb_add_comment_btn');
        } else {
          $commenttext = _sb('sb_comment_view');
          $commentplural = _sb('sb_comments_plural_view');
          $comment = _sb('sb_read_entry_btn');
        }

        // Add comment buttons
        if ( $comment_count == 0) {
          // [ add comment ]
          $entry_array[ 'comment' ][ 'name' ] = $comment;
        } else if ( $comment_count == 1) {
          // [ 1 comment ] (In Russian the number should come last.)
          if ( $lang_string[ 'sb_comment_btn_number_first' ] == true ) {
            $entry_array[ 'comment' ][ 'name' ] = $comment_count . ' ' . $commenttext;
          } else {
            $entry_array[ 'comment' ][ 'name' ] = $commenttext . ' ' . $comment_count;
          }
        } else {
          // [ n comments ] (In Russian the number should come last.)
          if ( $lang_string[ 'sb_comments_plural_btn_number_first' ] == true ) {
            $entry_array[ 'comment' ][ 'name' ] = $comment_count . ' ' . $commentplural;
          } else {
            $entry_array[ 'comment' ][ 'name' ] = $commentplural . ' ' . $comment_count;
          }
        }

        $entry_array[ 'comment' ][ 'comment_count' ] = $comment_count;

        // Add view counter
        if ( $view_counter > 0 ) {
          if ( $view_counter == 1) {
            $entry_array[ 'comment' ][ 'count' ] = _sb('sb_view_counter_pre') . $view_counter . _('sb_view_counter_post');
          } else {
            $entry_array[ 'comment' ][ 'count' ] = _sb('sb_view_counter_plural_pre') . $view_counter . _sb('sb_view_counter_plural_post');
          }
        }

        $entry_array[ 'entry' ] = replace_more_tag ( $entry_array[ 'entry' ] , false, $entry_array[ 'comment' ][ 'url' ] );

        // New 0.4.8
        if ( array_key_exists( 'IP-ADDRESS', $blog_entry_data ) ) {
          $entry_array[ 'ip-address' ] = $blog_entry_data[ 'IP-ADDRESS' ];
        }

        if ( array_key_exists( 'CREATEDBY', $blog_entry_data ) ) {
          $entry_array[ 'createdby' ][ 'text' ] = _sb('sb_postedby') . ' ' . Get_Fullname( $blog_entry_data[ 'CREATEDBY' ] );
          $entry_array[ 'createdby' ][ 'name' ] = Get_Fullname( $blog_entry_data[ 'CREATEDBY' ] );
          $entry_array[ 'avatarurl' ] = Get_AvatarUrl( $blog_entry_data[ 'CREATEDBY' ] ); 
        }

        $entry_array[ 'count' ] = $i;
        $entry_array[ 'maxcount' ] = count( $contents ) - 1;
        $entry_array[ 'logged_in' ] = $logged_in;
        $entry_array[ 'id' ] = $entry;

          $blog_content  .= theme_blogentry( $entry_array );
      }
    }

    $blog_content  .= '<br />';

    // Figure out page count - need this first for the First and Last links
    $pages_array = array();
    $current_page = 0;
    if ($category==NULL) {
     for ( $p = 0; $p < count( $entry_file_array ); $p += $blog_config->getTag('BLOG_MAX_ENTRIES') ) {
      array_push( $pages_array, $entry_file_array[ $p ] );
      if ($entry_index >= $p && $entry_index < $p + $blog_config->getTag('BLOG_MAX_ENTRIES')) {
        $current_page = count($pages_array)-1;
      }
     }
    } else {
      for ( $q = 0; $q < count( $cat_file_array ); $q += $blog_config->getTag( 'BLOG_MAX_ENTRIES' )) {
        array_push( $pages_array, $cat_file_array[ $q ] );
        if ($cat_entry_index >= $q && $cat_entry_index < $q + $blog_config->getTag( 'BLOG_MAX_ENTRIES')) {
          $current_page = count($pages_array)-1;
        }
      }
    }

    $blog_content  .= '<p style="text-align: center; font-weight: bold">';

    // Display First link if we are not on the first page
    if ($current_page > 0) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $pages_array[0] );
      $blog_content  .= '<span><a href="index.php?m=' . $month_dir . '&amp;y=' . $year_dir . '&amp;d=' . $d . '&amp;entry=' . sb_strip_extension( $entry_filename );
          if ( $category != NULL ) {
            $blog_content  .= '&amp;category=' . $category;
          }
      $blog_content  .= '">&#60;&#60;' . _sb('nav_first') . ' </a></span>';
    }

    // Display Back lin if required
    if ( $previous_entry != NULL ) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $previous_entry );
      $d = substr( $entry_filename, 9, 2 );
      $blog_content  .= '<span><a href="index.php?m=' . $month_dir . '&amp;y=' . $year_dir . '&amp;d=' . $d . '&amp;entry=' . sb_strip_extension( $entry_filename );
      if ( $category != NULL ) {
        $blog_content  .= '&amp;category=' . $category;
      }
      $blog_content  .= '"> &#60;' . _sb('nav_back') . ' </a></span> ';
    }

    // Display page count
    $pagestoshow = 10;
    if (count($pages_array) > 0) {
      $blog_content .= '<span>| ';

      $startpage = $current_page;

      // Test to see if we need to show previous pages in order to show all of the current visible pages
      $remainingpages = (count($pages_array) - $current_page);
      if ( $remainingpages < $pagestoshow ) {
        $startpage = $current_page - ($pagestoshow - $remainingpages);
      }
      if ( $startpage < 0 ) { $startpage = 0; }

      for ( $p = $startpage; $p < count( $pages_array ); $p++ ) {
        list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $pages_array[$p] );
        $d = substr( $entry_filename, 9, 2 );

        // Only show a limited number of page links at the bottom
        if ( $pagestoshow < 1 ) { Break; }

        if ($current_page == $p) {
          $blog_content  .= ($p + 1) . ' | ';
        } else {
          $blog_content  .= '<a href="index.php?m=' . $month_dir . '&amp;y=' . $year_dir . '&amp;d=' . $d . '&amp;entry=' . sb_strip_extension( $entry_filename );
          if ( $category != NULL ) {
            $blog_content  .= '&amp;category=' . $category;
          }
          $blog_content  .= '">' . ($p + 1) . '</a> | ';
        }
        $pagestoshow = $pagestoshow - 1;
      }
      $blog_content .= '</span>';
    }

    if ( $next_entry != NULL ) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $next_entry );
      $d = substr( $entry_filename, 9, 2 );
      $blog_content  .= ' <span><a href="index.php?m=' . $month_dir . '&amp;y=' . $year_dir . '&amp;d=' . $d . '&amp;entry=' . sb_strip_extension( $entry_filename );
      if ( $category != NULL ) {
        $blog_content  .= '&amp;category=' . $category;
      }
      $blog_content  .= '">' . _sb('nav_next') . '&#62; </a></span> ';
    }

    // Display Last link if we are not on the last page
    if ( $next_entry != NULL ) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $pages_array[count($pages_array)-1] );
      $blog_content  .= '<span><a href="index.php?m=' . $month_dir . '&amp;y=' . $year_dir . '&amp;d=' . $d . '&amp;entry=' . sb_strip_extension( $entry_filename );
          if ( $category != NULL ) {
            $blog_content  .= '&amp;category=' . $category;
          }

      $blog_content  .= '"> ' . _sb('nav_last') . '&#62;&#62;</a></span>';
    }
    $blog_content  .= '</p><br />';

    // Check for intervening static entries to be shown before current entries...

    // 2) Selected block (with or without border ie using CSS - without border handy for those with wide ads)
    if ( $blog_config->getTag('BLOG_ENABLE_STATIC_BLOCK') == true ) {
    $entry_array = array();
    $spec_block = get_specific_block( $blog_config->getTag('STATIC_BLOCK_OPTIONS') );
      if ( is_array( $spec_block ) ) {
        $entry_array[ 'entry' ] = $spec_block[ 'text' ];
        $entry_array[ 'subject' ] = $spec_block[ 'title' ];
        $bordertype = $blog_config->getTag('STATIC_BLOCK_BORDER');
        if ( $bordertype == 'noborder' ) {
          $blog_content = theme_genericentry( $entry_array, 'clear' ) . $blog_content;
        } else {
          $blog_content = theme_blogentry( $entry_array ) . $blog_content;
        }
      }
    }

    // 1) Search box
    if ( $blog_config->getTag('BLOG_SEARCH_TOP') == true ) {
      $entry_array = array();
      $search = array();
      $search = menu_search_field_horiz();
      $entry_array[ 'entry' ] = $search[ 'content' ];
      $blog_content = theme_genericentry( $entry_array, 'solid' ) . $blog_content;
    }

    return $blog_content;
  }

  function get_specific_block ( $title ) {
    // Create the right-hand block. Return single entry
    global $blog_content, $blog_subject, $blog_text, $blog_date, $user_colors, $logged_in;
    global $lang_string;

    // Read blocks file.
    $filename = CONFIG_DIR.'blocks.txt';
    $result = sb_read_file( $filename );

    // Match against title - nothing else to match against (no keys used)
    // Append new blocks.
    $block = array();
    if ( $result ) {
      $array = explode('|', $result);
      for ( $i = 0; $i < count( $array ); $i+=2 ) {
        $block[ 'title' ] = blog_to_html( $array[$i], false, false, false, true );
        $block[ 'text' ] = blog_to_html( $array[$i+1], false, false, false, true );
        if ( $block[ 'title' ] == $title ) {
          return ( $block );
        }
      }
    }
  }

  function get_fullname( $username ) {
    global $lang_string;

    // admin only
    if ( $username == 'admin' ) {
      $fullname = _sb('sb_admin');
      return ( $fullname );
    }

    // Go to the users database and get the user name
    if ( $username == '' ) {
      $fullname = _sb('sb_admin');
      return ( $fullname );
    } else {
      $user_list = read_users();
      foreach ($user_list as $tmp) {
        if ( $tmp[1] == $username ) {
          $fullname = $tmp[0];
          //fclose($pfile);
          return ( $fullname );
        }
      }
    }
    return ( _sb('sb_admin') );
  }

  function get_avatarurl( $username ) {
    // Go to the users database and get the user name
    if ( $username != '' ) {
      $user_list = read_users();
      foreach ($user_list as $tmp) {
        if ( $tmp[1] == $username ) {
          $avatarurl = $tmp[3];
          //fclose($pfile);
          return ( $avatarurl );
        }
      }
    }
    //if (isset($pfile)) {
    //  fclose($pfile);}
  }

  function CheckUserSecurity( $username, $type ) {
    // Go to the users database and get the user name
    if ( $username != '' ) {
      $answer = false;
      $user_list = read_users();
      foreach ($user_list as $tmp) {
        if ( $tmp[1] == $username ) {
          if ( ($type == 'MOD') and ($tmp[6] == 'Y')) {
            $answer = true;
          } elseif ( ($type == 'DEL') and ($tmp[7] == 'Y')) {
            $answer = true;
          } elseif ( ($type == 'EDIT') and ($tmp[8] == 'Y')) {
            $answer = true;
          }

          //fclose($pfile);
          return ( $answer );
        }
      }
    }
    return ( $answer );
  }

  function get_latest_entry () {
    // Figure out the date of the last entry. Set default year and month global values.
    // This is done so we're not displaying an empty page. If it's April and the last
    // entry was March, then we want to show March's entries (not April...)
    //
    // Returns nothing but sets $GLOBALS[ 'month' ] and $GLOBALS[ 'year' ]
    //
    $entry_array = blog_entry_listing();

    if ( count( $entry_array>0 ) ) {
      $GLOBALS[ 'year' ] = substr($entry_array[0], 5, 2);
      $GLOBALS[ 'month' ] = substr($entry_array[0], 7, 2);
      $GLOBALS[ 'day' ] = substr($entry_array[0], 9, 2);
    }
  }

  function blog_entry_listing () {
    global $blog_config;

    // Return listing of all the blog entries in order
    // of newest to oldest.

    $filename = CONFIG_DIR.'~blog_entry_listing.tmp';
    $entry_array = sb_read_file( $filename );
    if ( $entry_array != NULL ) {
      // Use cached array
      $entry_array = unserialize( $entry_array );
      rsort( $entry_array ); // Sort array newest to oldest
    } else {
      // Rebuild array.
      if ( $blog_config->getTag('BLOG_ENABLE_CACHE') == true ) {
        // sleep(1); // To avoid server overload
      }

      $basedir = CONTENT_DIR;

      // YEAR directories
      $entry_array = array();
      $dir = $basedir;
      if ( @is_dir( $dir ) ) {
        if ( $year_dir_handle = @opendir( $dir ) ) {
          while ( ( $year_dir = readdir( $year_dir_handle ) ) !== false ) {
            if ( is_dir( $dir . $year_dir ) ) {
              if ( $year_dir != '.' && $year_dir != '..' && $year_dir != 'static' ) {

                // MONTH directories

                if ( $month_dir_handle = @opendir( $dir . $year_dir . '/' ) ) {
                  while ( ( $month_dir = readdir( $month_dir_handle ) ) !== false ) {
                    if ( is_dir( $dir . $year_dir . '/' . $month_dir ) ) {
                      if ( $month_dir != '.' && $month_dir != '..' ) {

                        // ENTRIES

                        if ( $entry_dir_handle = @opendir( $dir . $year_dir . '/' . $month_dir . '/' ) ) {
                          while ( ( $entry_filename = readdir( $entry_dir_handle ) ) !== false ) {
                            if ( is_file( $dir . $year_dir . '/' . $month_dir . '/' . $entry_filename ) ) {

                              $ext = strtolower( strrchr( $entry_filename, '.' ) );
                              if ( $ext == '.txt' || $ext == '.gz' ) {

                                // Store Blog Entry Information
				$value = blog_entry_to_array($dir . $year_dir . '/' . $month_dir . '/' . $entry_filename);
                                array_push( $entry_array, implode( '|', array( $entry_filename, $year_dir, $month_dir, $value['SUBJECT'] ) ) );
                              }

                            }
                          }
                        }

                        // END of ENTRIES

                      }
                    }
                  }
                }

                // END of MONTH directories
              }
            }
          }
        }
      }

      rsort( $entry_array ); // Sort array newest to oldest

      // Check the option first to see if we use the cache
      if ( $blog_config->getTag('BLOG_ENABLE_CACHE') == true ) {
        // Do not create cache if empty
        if ( count( $entry_array )>0 ) {
          sb_write_file( $filename, serialize( $entry_array ) );
        }
      }
    }

    // Remove "future" entries
    $now = date('ymd-His', time());
    if ($GLOBALS['logged_in']==false) {
      for ($index=0; $index<count($entry_array); $index++) {
        if (substr($entry_array[$index], 5, 13)>$now) {
          array_splice($entry_array, $index, 1);
          $index--;
        }
      }
    }

    return( $entry_array );
  }

  // -----------------
  // Utility Functions
  // -----------------

  function entry_exists ( $y, $m, $entry ) {
    $entry_id = CONTENT_DIR.$y.'/'.$m.'/'.$entry;

    $exists = false;
    if ( file_exists( $entry_id . '.txt' ) ) {
      $exists = true;
    } elseif ( file_exists( $entry_id . '.txt.gz' ) ) {
      $exists = true;
    }

    return $exists;
  }

  function get_entry_title ( $y, $m, $entry ) {
    global $lang_string, $blog_config, $user_colors, $theme_vars;

    $entry_id = CONTENT_DIR . $y . '/' . $m . '/' . $entry;

    if ( file_exists( $entry_id . '.txt' ) ) {
      $filename = $entry_id . '.txt';
    } elseif ( file_exists( $entry_id . '.txt.gz' ) ) {
      $filename = $entry_id . '.txt.gz';
    }

    $blog_entry = blog_entry_to_array( $filename );

    if ($blog_entry == FALSE ) {
      $title = ""; // For some reason we couldnt load the file
    } else {
      $title = blog_to_html( $blog_entry[ 'SUBJECT' ], false, false ); // loaded and fired
    }

    return( $title );
  }

  // -----------------
  // Preview Functions
  // -----------------

  function preview_entry ( $blog_subject, $blog_text, $tb_ping, $temp_relatedlink, $timestamp ) {
    // Function to preview an entry before saving it to disk
    //
    // Just going through the motions...
    global $blog_content, $user_colors, $lang_string, $logged_in;

    if ( !isset( $timestamp ) ) {
      $timestamp = time();
    }
    $array = array( clean_post_text( $blog_subject ), $timestamp, clean_post_text( $blog_text ), clean_post_text( $tb_ping ), clean_post_text( $temp_relatedlink ) );
    $str = implode('|', $array);

    list( $blog_subject, $blog_date, $blog_text, $tb_ping, $relatedlink ) = explode( '|', $str );

    // // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
    $entry_array = array();
    $entry_array[ 'subject' ] = blog_to_html( $blog_subject, false, false, false, true );
    $entry_array[ 'date' ] = blog_to_html( format_date( $blog_date ), false, false );
    $entry_array[ 'entry' ] = blog_to_html( $blog_text, false, false, false, true ) . '<br />';
    if ( $tb_ping !== '' ) {
      $entry_array[ 'tb_ping' ] = blog_to_html( $tb_ping, false, false );
    }
    if ( $temp_relatedlink !== '' ) {
      $entry_array[ 'relatedlink' ][ 'name' ] = _sb('sb_relatedlink');
      $entry_array[ 'relatedlink' ][ 'url' ] = blog_to_html( $relatedlink, false, false );
    }

    $blog_content = theme_blogentry( $entry_array );

    return ( $blog_content );
  }

  function preview_static_entry ( $blog_subject, $blog_text ) {
    // Function to preview an entry before saving it to disk
    //
    // Just going through the motions...
    global $blog_content, $user_colors, $logged_in;

    $array = array( clean_post_text( $blog_subject ), time(), clean_post_text( $blog_text ) );
    $str = implode('|', $array);

    list( $blog_subject, $blog_date, $blog_text ) = explode( '|', $str );

    // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
    $entry_array = array();
    $entry_array[ 'subject' ] = blog_to_html( $blog_subject, false, false, false, true );
    $entry_array[ 'date' ] = blog_to_html( format_date( $blog_date ), false, false );
    $entry_array[ 'entry' ] = blog_to_html( $blog_text, false, false, false, true ) . '<br />';
    $entry_array[ 'categories_id'] = $temp_cat_array;

    $blog_content = theme_blogentry( $entry_array );

    return ( $blog_content );
  }

  function read_entry_from_file ( $entry_id ) {
    // Read an entry from disk and create the HTML.
    //
    // This function is used by:
    //    1). delete.php
    //    2). add.php
    //    3). sb_comments.php
    //
    if ( file_exists( $entry_id . '.txt' ) ) {
      $filename = $entry_id . '.txt';
    } elseif ( file_exists( $entry_id . '.txt.gz' ) ) {
      $filename = $entry_id . '.txt.gz';
    }

    $blog_entry_data = blog_entry_to_array( $filename );

    $entry_array = array();
    $entry_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, false, false, true );
    $entry_array[ 'date' ] = blog_to_html( format_date( $blog_entry_data[ 'DATE' ] ), false, false );
    $entry_array[ 'entry' ] = blog_to_html( $blog_entry_data[ 'CONTENT' ], false, false, false, true ) . '<br />';
    $entry_array[ 'id' ] = substr( $entry_id, strlen( $entry_id )-18, 18 );

    // Categories
    if ( array_key_exists( 'CATEGORIES', $blog_entry_data ) ) {
      $temp_cat_array = explode( ',', $blog_entry_data[ 'CATEGORIES' ] );
      $temp_cat_names = Array();
      for ( $j = 0; $j < count( $temp_cat_array ); $j++ ) {
        array_push( $temp_cat_names, get_category_by_id ( $temp_cat_array[$j] ) );
      }
      $entry_array[ 'categories' ] = $temp_cat_names;
      $entry_array[ 'categories_id'] = $temp_cat_array;
    }

    $blog_content = theme_blogentry( $entry_array );

    return ( $blog_content );
  }
?>
