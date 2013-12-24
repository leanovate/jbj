<?php
  // --------------------------
  // Simple PHP Blog Theme File
  // --------------------------
  //
  // Name: GLight
  // Author: Bill Bateman
  // Version: 0.5.0

  theme_init();

  // ---------------
  // Theme Variables
  // ---------------
  function theme_init () {
    global $theme_vars;

    $theme_vars = array();

    // Optional:
    // "content_width" and "menu_width" area used internally
    // within the theme only. (optional but recommended.)
    $theme_vars[ 'content_width' ] = 542;
    $theme_vars[ 'menu_width' ] = 200;

    // Required:
    // "popup_window" "width" and "height" are used to determine
    // the size of window to open for the comment view.
    $theme_vars[ 'popup_window' ][ 'width' ] = $theme_vars[ 'content_width' ] + 50;
    $theme_vars[ 'popup_window' ][ 'height' ] = 600;

    // Optional:
    // "popup_window" "content_width" is only used internally.
    $theme_vars[ 'popup_window' ][ 'content_width' ] = $theme_vars[ 'content_width' ];

    // Required:
    // Determines the maximum with of images within a page.
    // Make sure this value is less then "content_width" or you
    // might have wrapping problems.
    //
    // Also, the "66" number is somewhat determined by the css
    // styles that you have applied. If you adjust the margins or
    // padding then these will change.
    $theme_vars[ 'max_image_width' ] = $theme_vars[ 'content_width' ] - 66;

    // ------------
    // CUSTOMIZATION
    // ------------
    // New 0.3.8
    $theme_vars[ 'menu_align' ] = 'left'; // Valid values are 'left' or 'right'
  }

  // Function:
  // theme_blogentry( $entry_array )
  //
  // Theme for Blog Entries
  // ----------------------
  // All data is stored the $entry_array associative array and
  // passed to the function. Keep in mind that multiple languages
  // are used. So, try not to hard-code "english" strings. If
  // you are creating graphics for buttons, try to use icons
  // instead of words.
  //
  // (Please note, the "\n" at the end of the echo() command adds
  // a return charater in the HTML source. This is standard PHP
  // behavior. Note the "\n" will not work for this. It has to be
  // surrounded by double-quotes.)
  //
  // The array could contains the following keys:
  // $entry_array[ 'subject' ]          = String: Subject line
  // $entry_array[ 'date' ]             = String: Date posted in the appropriate language and format.
  // $entry_array[ 'entry' ]            = String: The body of the blog entry
  // $entry_array[ 'logged_in' ]       = Boolean: True if user is logged in (used to determine whether to show "edit" and "delete" buttons)
  // $entry_array[ 'edit' ][ 'url' ]      = String: URL
  // $entry_array[ 'edit' ][ 'name' ]     = String: The word "edit" in the appropriate language.
  // $entry_array[ 'delete' ][ 'url' ]    = String: URL
  // $entry_array[ 'delete' ][ 'name' ]   = String: The word "delete" in the appropriate language.
  // $entry_array[ 'ban' ][ 'url' ]    = String: URL
  // $entry_array[ 'ban' ][ 'name' ]   = String: The word "ban" in the appropriate language.
  // $entry_array[ 'comment' ][ 'url' ]   = String: URL
  // $entry_array[ 'comment' ][ 'name' ]  = String: This will be "add comment", "1 comment", or "2 comments" in the appropriate language.
  // $entry_array[ 'comment' ][ 'count' ] = String: The number of "views" in the appropriate language.
  // $entry_array[ 'count' ]            = Integer: Index of current entry (i.e. use this if you want to add a line after every entry except the last one...)
  // $entry_array[ 'maxcount' ]         = Integer: Total number of entries
  // $entry_array[ 'categories' ]         = array: String names of categories
  // $entry_array[ 'createdby' ][ 'text' ] = String: Posted by <users display name>
  // $entry_array[ 'createdby' ][ 'name' ] = String: <users display name>
  // (There are some more, just look through the code...)
  function theme_blogentry ( $entry_array, $mode='entry' ) { // New 0.4.8
    global $user_colors, $blog_theme, $blog_config;

    // Default image path.
    $img_path = BASEURL . "themes/" . $blog_theme . "/images/";

    $blog_content = "";
    $blog_content = $blog_content . "\n<!-- BLOG ENTRY BEGIN -->\n";

    // New 0.4.4
    // You must have this if you are using the trackback feature.
    if ( $blog_config->getTag('BLOG_TRACKBACK_ENABLED')) {
      $blog_content = $blog_content . "<!--\n";
      $blog_content = $blog_content . '<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"' . "\n";
      $blog_content = $blog_content . '         xmlns:dc="http://purl.org/dc/elements/1.1/"' . "\n";
      $blog_content = $blog_content . '         xmlns:trackback="http://madskills.com/public/xml/rss/module/trackback/">' . "\n";
      $blog_content = $blog_content . '<rdf:Description' . "\n";
      $blog_content = $blog_content . '    rdf:about="' . $entry_array[ 'permalink' ][ 'url' ] . '"' . "\n";
      $blog_content = $blog_content . '    dc:identifier="' . $entry_array[ 'permalink' ][ 'url' ] . '"' . "\n";
      $blog_content = $blog_content . '    dc:title="' . $entry_array[ 'subject' ] . '"' . "\n";
      $blog_content = $blog_content . '    trackback:ping="' . $entry_array[ 'trackback' ][ 'ping_url' ] . '" />' . "\n";
      $blog_content = $blog_content . '</rdf:RDF>' . "\n";
      $blog_content = $blog_content . '-->' . "\n";
    }

    // Display SUBJECT Line
    $blog_content = $blog_content . "\n<!-- BLOG TITLE BEGIN -->\n";
    $blog_content = $blog_content . '<div class="entry_top">
                                     <img width="2" height="2" src="' . $img_path . 'corner_tl.gif" alt="" style="float: left" />
                                     <img width="2" height="2" src="' . $img_path . 'corner_tr.gif" alt="" style="float: right" />
                                     </div>' . "\n";
    $blog_content = $blog_content . '<div class="blog_subject">';
    if ( $entry_array[ 'avatarurl' ] != '' ) {
      $blog_content = $blog_content . '<img src="' . $entry_array[ 'avatarurl'] . '" alt="" style="float: left" />';  }

    $blog_content = $blog_content . $entry_array[ 'subject' ];
    if (!empty($entry_array[ 'id' ])) {
        $blog_content .= '<a id="' . $entry_array[ 'id' ] . '">&nbsp;</a>';
    }
    $blog_content .= "\n";

    $blog_content = $blog_content . "</div>\n";
    $blog_content = $blog_content . "\n<!-- BLOG TITLE END -->\n";

    $blog_content = $blog_content . "\n<!-- BLOG BODY BEGIN -->\n";
    $blog_content = $blog_content . '<div class="blog_body">' . "\n\t";

    // Display DATE
    if ( $mode != 'static') { // New 0.4.8
      $blog_content = $blog_content . "<div class=\"blog_date\">" . $entry_array[ 'date' ];

      // Display CATEGORIES
      // This is an array. There can be multiple categories per entry.
      if ( array_key_exists( "categories", $entry_array ) ) {
        $blog_content = $blog_content . " - ";
        for ( $i = 0; $i < count( $entry_array[ 'categories' ] ); $i++ ) {
          $blog_content .= '<a href="index.php?category=' . $entry_array[ 'categories_id' ][$i] . '">' . $entry_array[ 'categories' ][$i] . '</a>';
          if ( $i < count( $entry_array[ 'categories' ] ) - 1 ) {
            $blog_content .= ', ';
          }
        }
      }

      // New 0.4.8
      if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {
        if ( array_key_exists( 'ip-address', $entry_array ) && $mode == 'comment' ) {
          $blog_content = $blog_content . ' <span class="blog_ip_address">&lt;&nbsp;' . $entry_array[ 'ip-address' ] . '&nbsp;&gt;</span>' . "\n";
        }
      }

      if ( isset( $entry_array[ 'createdby' ][ 'text' ] )) {
          $blog_content = $blog_content . '<br />' . $entry_array[ 'createdby' ][ 'text'];
      }

      $blog_content = $blog_content . "</div>\n\t\t";
    }

    // Display BODY TEXT
    $blog_content = $blog_content . $entry_array[ 'entry' ];
    $blog_content = $blog_content . "\n\t</div>";
    $blog_content = $blog_content . "\n<!-- BLOG BODY END -->\n";

    $blog_content = $blog_content . "\n<!-- BLOG FOOTER BEGIN -->\n";

    // Display ADD COMMENT, COUNT,TRACKBACK, PERMALINK, and RATING
    $comment_area = "";
    // Display EDIT and DELETE buttons if the user is logged in.
    if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {

      $comment_area = $comment_area . "\t" . '<span class="blog_title_buttons">' . "\n";

      // Show "edit" and "delete" buttons if the user is logged-in...
      if ( isset( $entry_array[ 'edit' ][ 'url' ] ) ) {
        $comment_area = $comment_area . "\t\t" . '<a href="' . $entry_array[ 'edit' ][ 'url' ] . '">' . $entry_array[ 'edit' ][ 'name' ] . '</a> / ' . "\n";
      }
      if ( isset( $entry_array[ 'delete' ][ 'url' ] ) ) {
        $comment_area = $comment_area . "\t\t" . '<a href="' . $entry_array[ 'delete' ][ 'url' ] . '">' . $entry_array[ 'delete' ][ 'name' ] . '</a> / ' . "\n";
      }

      if ( isset( $entry_array[ 'ban' ][ 'url' ] ) ) {
        $comment_area = $comment_area . "\t\t" . '<a href="' . $entry_array[ 'ban' ][ 'url' ] . '">' . $entry_array[ 'ban' ][ 'name' ] . '</a> / ' . "\n";
      }

      $comment_area = $comment_area . "\t</span>\n";
    }

    if ( isset( $entry_array[ 'comment' ][ 'url' ] ) ) {
      // Show "add comment" button if set...
      $comment_area = $comment_area . '<a href="' . $entry_array[ 'comment' ][ 'url' ] . '">' . $entry_array[ 'comment' ][ 'name' ] . ' </a>' . "\n";
    }

    if ( isset( $entry_array[ 'comment' ][ 'count' ] ) ) {
      // Show "( x views )" string...
      $comment_area = $comment_area . " ( " . $entry_array[ 'comment' ][ 'count' ] . " )\n";
    }

    if ( isset( $entry_array[ 'trackback' ][ 'url' ] ) ) {
      // Show 'trackback' string...
      $comment_area = $comment_area . '&nbsp;&nbsp;|&nbsp;&nbsp;<a href="' . $entry_array[ 'trackback' ][ 'url' ] . '">' . $entry_array[ 'trackback' ][ 'name' ] . '</a>' . "\n";
    }

    if ( $blog_config->getTag('BLOG_ENABLE_PERMALINK') ){ // New for 0.4.6
      if ( isset( $entry_array[ 'permalink' ][ 'url' ] ) ) {
        // Show 'permalink' string...
        $comment_area = $comment_area . '&nbsp;&nbsp;|&nbsp;&nbsp;<a href="' . $entry_array[ 'permalink' ][ 'url' ] . '">' . $entry_array[ 'permalink' ][ 'name' ] . '</a>' . "\n";
      }
    }

    if ( isset( $entry_array['relatedlink']['url'] ) ) {
      // Show 'relatedlink' symbol - New to 0.4.6
      $comment_area = $comment_area . '&nbsp;&nbsp;|&nbsp;&nbsp;<a href="' . $entry_array['relatedlink']['url'] . '">' . $entry_array['relatedlink']['name'] . '</a>' . "\n";
    }

    if ( isset( $entry_array[ 'stars' ] ) ) {
      // Show 'rating' stars...
      $comment_area = $comment_area . '&nbsp;&nbsp;|&nbsp;&nbsp;' . $entry_array[ 'stars' ] . "\n";
    }

    if ( $comment_area != "" ) {
      $blog_content = $blog_content . "\n\t<div class=\"blog_comment\">" . $comment_area . "</div>\n";
    }
    $blog_content = $blog_content . '<div class="entry_bottom">
                                     <img width="2" height="2" src="' . $img_path . 'corner_bl.gif" alt="" style="float: left" />
                                     <img width="2" height="2" src="' . $img_path . 'corner_br.gif" alt="" style="float: right" />
                                     </div>' . "\n";
    $blog_content = $blog_content . "\n<!-- FOOTER ENTRY END -->\n";

    $blog_content = $blog_content . "<br />";

    $blog_content = $blog_content . "\n<!-- BLOG ENTRY END -->\n";

    return $blog_content;
  }

  function theme_genericentry ( $entry_array, $style='normal' ) { // New 0.5.0
    global $user_colors, $blog_theme, $blog_config;

    // init vars
    $img_path = "themes/" . $blog_theme . "/images/";
    $blog_content = '';
    $blog_content = $blog_content . "\n<!-- STATIC NO HEADER ENTRY START -->\n";

    // Text of entry

    if ( $style == 'solid' ) {
      $blog_content = $blog_content . '<div class="blog_body_solid">' . "\n\t";
    } elseif ( $style == 'clear' ) {
      $blog_content = $blog_content . '<div class="blog_body_clear">' . "\n\t";
    } else {
      $blog_content = $blog_content . '<div class="blog_body_framed">' . "\n\t";
    }

    $blog_content = $blog_content . $entry_array[ 'entry' ];
    $blog_content = $blog_content . "\n\t</div>";

    $blog_content = $blog_content . "<br />";

    $blog_content = $blog_content . "\n<!-- STATIC NO HEADER ENTRY END -->\n";

    return $blog_content;
  }

  function theme_staticentry ( $entry_array ) {
    // Display STATIC entry page.
    //
    // This theme uses the same format for static entries as regular blog entries.
    $blog_content = theme_blogentry( $entry_array, 'static' );
    return $blog_content;
  }

  function theme_commententry ( $entry_array ) {
    // Display COMMENT entry page.
    //
    // This theme uses the same format for comment entries as regular blog entries.
    // $blog_content = theme_blogentry( $entry_array, 'comment' );
    global $user_colors, $blog_theme, $blog_config;

    // Default image path.
    $img_path = "themes/" . $blog_theme . "/images/";

    $blog_content = "";
    $blog_content = $blog_content . "\n<!-- BLOG ENTRY BEGIN -->\n";

    // Display SUBJECT Line
    $blog_content = $blog_content . "\n<!-- BLOG TITLE BEGIN -->\n";
    $blog_content = $blog_content . '<div class="entry_top_comment">
                                     <img width="2" height="2" src="' . $img_path . 'corner_tl.gif" alt="" style="float: left" />
                                     <img width="2" height="2" src="' . $img_path . 'corner_tr.gif" alt="" style="float: right" />
                                     </div>' . "\n";
    $blog_content = $blog_content . '<div class="blog_subject_comment">' . $entry_array[ 'subject' ] . '<a id="' . $entry_array[ 'id' ] . '">&nbsp;</a>' . "\n";

    $blog_content = $blog_content . "</div>\n";
    $blog_content = $blog_content . "\n<!-- BLOG TITLE END -->\n";

    $blog_content = $blog_content . "\n<!-- BLOG BODY BEGIN -->\n";
    $blog_content = $blog_content . '<div class="blog_body_comment">' . "\n\t";

    // Display DATE
    if ( $mode != 'static') { // New 0.4.8
      $blog_content = $blog_content . "<div class=\"blog_date\">" . $entry_array[ 'date' ];

      // Display CATEGORIES
      // This is an array. There can be multiple categories per entry.
      if ( array_key_exists( "categories", $entry_array ) ) {
        $blog_content = $blog_content . " - ";
        for ( $i = 0; $i < count( $entry_array[ 'categories' ] ); $i++ ) {
          $blog_content .= '<a href="index.php?category=' . $entry_array[ 'categories_id' ][$i] . '">' . $entry_array[ 'categories' ][$i] . '</a>';
          if ( $i < count( $entry_array[ 'categories' ] ) - 1 ) {
            $blog_content .= ', ';
          }
        }
      }

      // New 0.4.8
      if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {
        if ( array_key_exists( 'ip-address', $entry_array ) ) {
          $blog_content = $blog_content . ' <span class="blog_ip_address">&lt;&nbsp;' . $entry_array[ 'ip-address' ] . '&nbsp;&gt;</span>' . "\n";
        }
      }

      if ( isset( $entry_array[ 'createdby' ][ 'text' ] )) {
          $blog_content = $blog_content . '<br />' . $entry_array[ 'createdby' ][ 'text'];
      }

      $blog_content = $blog_content . "</div>\n\t\t";
    }

    // Display BODY TEXT
    $blog_content = $blog_content . $entry_array[ 'entry' ];
    $blog_content = $blog_content . "\n\t</div>";
    $blog_content = $blog_content . "\n<!-- BLOG BODY END -->\n";

    $blog_content = $blog_content . "\n<!-- BLOG FOOTER BEGIN -->\n";

    // Display ADD COMMENT, COUNT,TRACKBACK, PERMALINK, and RATING
    $comment_area = "";
    // Display EDIT and DELETE buttons if the user is logged in.
    if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {

      $comment_area = $comment_area . "\t" . '<span class="blog_title_buttons">' . "\n";

      // Show "ban" and "delete" buttons if the user is logged-in...
      if ( isset( $entry_array[ 'delete' ][ 'url' ] ) ) {
        $comment_area = $comment_area . "\t\t" . '<a href="' . $entry_array[ 'delete' ][ 'url' ] . '">' . $entry_array[ 'delete' ][ 'name' ] . '</a> / ' . "\n";
      }

      if ( isset( $entry_array[ 'ban' ][ 'url' ] ) ) {
        $comment_area = $comment_area . "\t\t" . '<a href="' . $entry_array[ 'ban' ][ 'url' ] . '">' . $entry_array[ 'ban' ][ 'name' ] . '</a> / ' . "\n";
      }

      $comment_area = $comment_area . "\t</span>\n";
    }


    if ( $comment_area != "" ) {
      $blog_content = $blog_content . "\n\t<div class=\"blog_comment_comment\">" . $comment_area . "</div>\n";
    }

    $blog_content = $blog_content . '<div class="entry_bottom_comment">
                                     <img width="2" height="2" src="' . $img_path . 'corner_bl.gif" alt="" style="float: left" />
                                     <img width="2" height="2" src="' . $img_path . 'corner_br.gif" alt="" style="float: right" />
                                     </div>' . "\n";
    $blog_content = $blog_content . "\n<!-- FOOTER ENTRY END -->\n";

    $blog_content = $blog_content . "<br />";

    $blog_content = $blog_content . "\n<!-- BLOG ENTRY END -->\n";

    return $blog_content;
  }

  // New 0.3.8
  function theme_trackbackentry ( $entry_array ) {
    global $blog_config, $blog_theme, $user_colors;

    $img_path = 'themes/' . $blog_theme . '/images/';

    $blog_content = "";
    $blog_content = $blog_content . "\n" . '<!-- BLOG ENTRY BEGIN -->' . "\n";

    // SUBJECT
    $blog_content = $blog_content . '<div class="blog_title">' . $entry_array[ 'title' ] . "\n";

    if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {
      $blog_content = $blog_content . "\t" . '<span class="blog_title_buttons">' . "\n";

      // Show 'delete' button if the user is logged-in...
      if ( isset( $entry_array[ 'delete' ][ 'url' ] ) ) {
        $blog_content = $blog_content . "\t\t" . '<a href="' . $entry_array[ 'delete' ][ 'url' ] . '"><img src="' . $img_path . 'box_cancel.png" alt="' . $entry_array[ 'delete' ][ 'name' ] . '" width="14" height="14" /> </a>' . "\n";
      }

      $blog_content = $blog_content . "\t" . '</span>' . "\n";
    }

    $blog_content = $blog_content . '</div>' . "\n";

    $blog_content = $blog_content . '<div class="blog_body">' . "\n\t";
    $blog_content = $blog_content . '<div class="blog_date">' . $entry_array[ 'date' ] . '</div>' . "\n\t\t";

    // Blog content body...
    $blog_content = $blog_content . $entry_array[ 'excerpt' ] . "<p>\n";

    if ( (isset( $entry_array[ 'blog_name' ] ) ) && ($entry_array[ 'blog_name' ] != "") ) {
       $blog_content = $blog_content . '<a href="'.$entry_array[ 'url' ].'" target="_blank">[ ' . $entry_array[ 'blog_name' ] . " ]</a><p>\n";
    } else {
       $blog_content = $blog_content . '<a href="'.$entry_array[ 'url' ].'" target="_blank">[ ' . $entry_array[ 'url' ] . " ]</a><p>\n";
    }

    $blog_content = $blog_content . "\n\t" . '</div>';

    $blog_content = $blog_content . "<br />";

    $blog_content = $blog_content . "\n" . '<!-- BLOG ENTRY END -->' . "\n";

    return $blog_content;
  }

  // Function:
  // theme_default_colors( )
  //
  // Default Base Colors
  // -------------------
  // $user_colors is an associative array that stores
  // all color information. These are the default colors
  // for the theme. These values are read in, and then
  // get overwritten when the user saved colors are
  // read from file.
  //
  // Note
  // ----
  // You can create your own "keys" but they will not
  // show up in the "colors.php" document yet...
  //
  // Also, only these default keys have translations for
  // different languages. If something is missing, email
  // me and I'll add it for future releases.
  //
  // Eventually you'll have the option of disabling keys
  // and added keys will appear on the "color.php" page.
  function theme_default_colors () {
    global $lang_string;

    $color_def = array();

    // Backgrounds
    array_push( $color_def, array( 'id' => 'bg_color',
                'string' => $lang_string[ 'bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'header_bg_color',
                'string' => $lang_string[ 'header_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'footer_bg_color',
                'string' => $lang_string[ 'footer_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'main_bg_color',
                'string' => $lang_string[ 'main_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'menu_bg_color',
                'string' => $lang_string[ 'menu_bg_color' ],
                'default' => 'FFFFFF' ) );

    // Borders
    array_push( $color_def, array( 'id' => 'border_color',
                'string' => $lang_string[ 'border_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'inner_border_color',
                'string' => $lang_string[ 'inner_border_color' ],
                'default' => 'B1C3E7' ) );
    array_push( $color_def, array( 'id' => 'header_txt_color',
                'string' => $lang_string[ 'header_txt_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'footer_txt_color',
                'string' => $lang_string[ 'footer_txt_color' ],
                'default' => '666666' ) );
    array_push( $color_def, array( 'id' => 'txt_color',
                'string' => $lang_string[ 'txt_color' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'headline_txt_color',
                'string' => $lang_string[ 'headline_txt_color' ],
                'default' => '4F4F48' ) );
    array_push( $color_def, array( 'id' => 'date_txt_color',
                'string' => $lang_string[ 'date_txt_color' ],
                'default' => '999999' ) );


    array_push( $color_def, array( 'id' => 'entry_bg',
                'string' => $lang_string[ 'entry_bg' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'entry_title_bg',
                'string' => $lang_string[ 'entry_title_bg' ],
                'default' => 'E5ECF9' ) );
    array_push( $color_def, array( 'id' => 'entry_border',
                'string' => $lang_string[ 'entry_border' ],
                'default' => 'B1C3E7' ) );

    array_push( $color_def, array( 'id' => 'entry_title_text',
                'string' => $lang_string[ 'entry_title_text' ],
                'default' => '4F4F48' ) );
    array_push( $color_def, array( 'id' => 'entry_text',
                'string' => $lang_string[ 'entry_text' ],
                'default' => '000000' ) );

    array_push( $color_def, array( 'id' => 'menu_bg',
                'string' => $lang_string[ 'menu_bg' ],
                'default' => 'EEF2F8' ) );
    array_push( $color_def, array( 'id' => 'menu_title_bg',
                'string' => $lang_string[ 'menu_title_bg' ],
                'default' => 'C7D1E7' ) );
    array_push( $color_def, array( 'id' => 'menu_border',
                'string' => $lang_string[ 'menu_border' ],
                'default' => 'C7D1E7' ) );

    array_push( $color_def, array( 'id' => 'menu_title_text',
                'string' => $lang_string[ 'menu_title_text' ],
                'default' => '4F4F48' ) );
    array_push( $color_def, array( 'id' => 'menu_text',
                'string' => $lang_string[ 'menu_text' ],
                'default' => 'FFFFFF' ) );

    array_push( $color_def, array( 'id' => 'link_reg_color',
                'string' => $lang_string[ 'link_reg_color' ],
                'default' => '4F4F48' ) );
    array_push( $color_def, array( 'id' => 'link_hi_color',
                'string' => $lang_string[ 'link_hi_color' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'link_down_color',
                'string' => $lang_string[ 'link_down_color' ],
                'default' => '000000' ) );

    array_push( $color_def, array( 'id' => 'menu_link_reg_color',
                'string' => $lang_string[ 'menu_link_reg_color' ],
                'default' => '4F4F48' ) );
    array_push( $color_def, array( 'id' => 'menu_link_hi_color',
                'string' => $lang_string[ 'menu_link_hi_color' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'menu_link_down_color',
                'string' => $lang_string[ 'menu_link_down_color' ],
                'default' => '000000' ) );

    return ( $color_def );
  }

  // Function:
  // theme_pagelayout( )
  //
  // Page Layout Container/Wrapper
  // -----------------------------
  // This function controls all HTML output to the browser.
  //
  // Invoking the page_content() fuction inserts the actual
  // contents of the page.
  //
  function theme_pagelayout () {
    global $user_colors, $blog_theme, $blog_config, $theme_vars;

    // New 0.3.8
    //
    // You can change the width of the CONTENT area and MENU areas by
    // editing these variable in the "theme_init()" function at the top of
    // this file.
    $content_width = $theme_vars[ 'content_width' ];
    $menu_width = $theme_vars[ 'menu_width' ];
    $page_width = $content_width + $menu_width;

    // Default image path.
    $img_path = "themes/" . $blog_theme . "/images/";

    $header_graphic = $blog_config->getTag('BLOG_HEADER_GRAPHIC');
    if ( $header_graphic == '' ) {
      $header_graphic = $img_path . 'header750x100.jpg';
    }

    // Begin Page Layout HTML
    ?>
    <body>
      <div id="page">
            <div id="header">
            <?php
            if ( $blog_config->getTag('BLOG_ENABLE_TITLE')) { // New for 0.4.6
            echo($blog_config->getTag('BLOG_TITLE'));
            }?>
	    </div>

            <div id="pagebody">
                    <div id="sidebar">
                      <?php theme_menu(); ?>
                    </div>
                    <div id="maincontent">
                      <?php page_content(); ?>
                    </div>
                    <div id="footer"><p><?php echo($blog_config->getTag('BLOG_FOOTER')); ?> - <?php echo( page_generated_in() ); ?></p></div>
            </div>
      </div>
    </body>
    <?php
    // End Page Layout HTML
  }

  function theme_menu_block ($blockArray, $comment='MENU BLOCK', $toggleDiv=null) {
    global $blog_theme;
    $toggleDiv = str_replace(' ', '', $toggleDiv);

    // This function creates the menu "blocks" in the sidebar.
    //
    // If you don't want the block to have a "twisty" arrow, then don't pass in a value for $toggleDiv

    // With "twisty" arrow
    /*
      <!-- LINKS -->
      <div class="menu_title"><a id="linkSidebarLinks" onclick="toggleBlock('SidebarLinks');"><img src="themes/modern/images/minus.gif" alt="twisty"> Links</a></div>
      <div id="toggleSidebarLinks" class="menu_body">
      <a href="index.php">Home</a><br />
      </div><br />
    */

    // Without "twisty" arrow
    /*
      <!-- LINKS -->
      <div class="menu_title">Links</div>
      <div class="menu_body">
      <a href="index.php">Home</a><br />
      </div><br />
    */

    if ( isset( $blockArray[ 'content' ] ) && $blockArray[ 'content' ] != '' ) {
      // Default image path.
      $img_path = "themes/" . $blog_theme . "/images/";
      $img_show = $img_path . 'plus.gif';
      $img_hide = $img_path . 'minus.gif';

      echo( "\n<!-- " . $comment . " -->\n" );

      echo( '<div class="menu_title">' );
      if ( isset( $toggleDiv ) ) {
        echo( '<a id="link' . $toggleDiv . '" onclick="toggleBlock(\'' . $toggleDiv . '\');"><img height="11" width="11" src="' . $img_hide . '" alt="twisty" /> ' );
      }
      echo( $blockArray[ 'title' ] );
      if ( isset( $toggleDiv ) ) {
        echo( '</a>' );
      }
      echo( "</div>\n" );

      if ( isset( $toggleDiv ) ) {
        echo( '<div id="toggle' . $toggleDiv . '" class="menu_body">' . "\n" );
      } else {
        echo( '<div class="menu_body">' . "\n" );
      }
      echo( $blockArray[ 'content' ] . "\n" );
      echo( "</div><br />\n" );

      return true;
    } else {
      return false;
    }
  }

  function theme_menu () {
    global $user_colors, $lang_string, $theme_vars, $logged_in, $sb_info, $blog_config;

    // This function creates the sidebar menu.
    //
    // Move blocks of code up/down to change the order.
    //
    // The "\n" that you see is a RETURN character.
    // This is just to make the HTML code look prettier.
    // It will not show up on the page.
    //
    //  Please note that \n must be used within " quotes...
    //    echo( "\n" ); // <-- This is a return character
    //    echo( '\n' ); // <-- This will print \n on your page...
    //
    // You can use either ' or " in your echo() statements.
    // But keep in mind that might need to use a backslash --> \
    // to print a double or single quote:
    //
    //  These are equivalent: (note the \" or \'  escape chracter...)
    //    echo( 'this "is" a test' ); // displays: this "is" a test
    //    echo( "this \"is\" a test" );  // displays: this "is" a test
    //    echo( "this 'is' a test" );  // displays: this 'is' a test
    //    echo( 'this \'is\' a test' );  // displays: this 'is' a test

    echo( "\n<!-- SIDEBAR MENU BEGIN -->\n" );

    // CUSTOM BLOCKS
    $array = read_blocks($logged_in);
    for ($i=0 ; $i<count($array) ; $i+=2) {
      $result = Array();
      $result[ 'title' ] = $array[$i];
      $result[ 'content' ] = $array[$i+1];
      theme_menu_block( $result, $result[ 'title' ], 'Sidebar' . $result[ 'title' ] );
    }
  }
?>
