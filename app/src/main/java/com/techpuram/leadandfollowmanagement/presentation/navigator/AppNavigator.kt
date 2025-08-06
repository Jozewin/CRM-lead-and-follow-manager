package com.techpuram.leadandfollowmanagement.presentation.navigator

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.presentation.contact.addEdit.AddOrUpdateContactScreen
import com.techpuram.leadandfollowmanagement.presentation.contact.list.ContactListScreen
import com.techpuram.leadandfollowmanagement.presentation.contact.detail.ContactDetailsScreen
import com.techpuram.leadandfollowmanagement.presentation.deal.addEdit.AddEditDealEvent
import com.techpuram.leadandfollowmanagement.presentation.deal.addEdit.DealAddEditScreen
import com.techpuram.leadandfollowmanagement.presentation.deal.addEdit.AddEditDealViewModel
import com.techpuram.leadandfollowmanagement.presentation.deal.detail.DealDetailScreen
import com.techpuram.leadandfollowmanagement.presentation.followup.addEdit.AddEditFollowUpEvent
import com.techpuram.leadandfollowmanagement.presentation.followup.addEdit.FollowUpAddEditScreen
import com.techpuram.leadandfollowmanagement.presentation.followup.addEdit.AddEditFollowUpViewModel
import com.techpuram.leadandfollowmanagement.presentation.home.BottomNavigationBar
import com.techpuram.leadandfollowmanagement.presentation.home.TabBarItem
import com.techpuram.leadandfollowmanagement.presentation.lead.list.LeadListScreen
import com.techpuram.leadandfollowmanagement.presentation.lead.addEdit.AddOrUpdateLeadScreen
import com.techpuram.leadandfollowmanagement.presentation.lead.detail.LeadDetailScreen
import com.techpuram.leadandfollowmanagement.presentation.lead.selectContact.SelectContactScreen
import com.techpuram.leadandfollowmanagement.presentation.navgraph.Route
import com.techpuram.leadandfollowmanagement.presentation.more.MoreScreen
import com.techpuram.leadandfollowmanagement.presentation.more.customField.CustomFieldsScreen
import com.techpuram.leadandfollowmanagement.presentation.followup.list.FollowUpListScreen
import com.techpuram.leadandfollowmanagement.presentation.followup.detail.FollowUpDetailScreen
import com.techpuram.leadandfollowmanagement.presentation.deal.list.DealListScreen
import com.techpuram.leadandfollowmanagement.presentation.lead.addEdit.AddEditLeadEvent
import com.techpuram.leadandfollowmanagement.presentation.lead.addEdit.AddEditLeadViewModel
import com.techpuram.leadandfollowmanagement.presentation.more.backupRestore.BackupRestoreScreen
import com.techpuram.leadandfollowmanagement.presentation.util.ComingSoonScreen
import com.techpuram.leadandfollowmanagement.presentation.util.WebViewScreen
import com.techpuram.leadandfollowmanagement.util.AdManager
import com.techpuram.leadandfollowmanagement.util.PreferenceManager
import java.net.URLDecoder
import androidx.compose.ui.platform.LocalContext

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavigator(followUpId: Int? = null) {
    val context = LocalContext.current
    val shouldShowAds = remember { PreferenceManager.shouldShowAds(context) }
    
    val bottomNavigationItem = remember {
        listOf(
            TabBarItem(
                title = "Follow Up",
                selectedIcon = R.drawable.follow_up_filled,
                unselectedIcon = R.drawable.follow_up_outline
            ),
            TabBarItem(
                title = "Deals",
                selectedIcon = R.drawable.handshake_filled,
                unselectedIcon = R.drawable.handshake_outline
            ),
            TabBarItem(
                title = "Lead",
                selectedIcon = R.drawable.person_filled,
                unselectedIcon = R.drawable.person_outlined
            ),
            TabBarItem(
                title = "Contact",
                selectedIcon = R.drawable.address_book_filled,
                unselectedIcon = R.drawable.address_book_outline
            ),
            TabBarItem(
                title = "More",
                selectedIcon = R.drawable.settings_filled,
                unselectedIcon = R.drawable.settings_outlined
            )
        )
    }

    var fromFollowUpBottomNav by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    // Handle notification navigation - FIXED VERSION
    LaunchedEffect(followUpId) {
        if (followUpId != null) {
            // Add a small delay to ensure NavHost is fully composed
            kotlinx.coroutines.delay(100)
            // Navigate directly to the detail screen
            navController.navigate(Route.FollowUpDetail.createRoute(followUpId)) {
                // Clear the back stack to prevent navigation issues
                popUpTo(Route.FollowUp.route) {
                    inclusive = false
                }
            }
        }
    }

    val backstackState = navController.currentBackStackEntryAsState().value

    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    selectedItem = remember(key1 = backstackState) {
        when (backstackState?.destination?.route) {
            Route.FollowUp.route -> 0
            Route.Deals.route -> 1
            Route.LeadList.route -> 2
            Route.Contact.route -> 3
            Route.More.route -> 4
            else -> 0
        }
    }

    val isBottomBarVisible = remember(key1 = backstackState) {
        backstackState?.destination?.route == Route.FollowUp.route ||
                backstackState?.destination?.route == Route.Deals.route ||
                backstackState?.destination?.route == Route.LeadList.route ||
                backstackState?.destination?.route == Route.Contact.route ||
                backstackState?.destination?.route == Route.More.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible && fromFollowUpBottomNav == false) {
                Column {
                    // Show ads only from 2nd day onwards
                    if (shouldShowAds) {
                        AdManager.AdBanner(
                            modifier = Modifier
                                .fillMaxWidth(),
                            adId = AdManager.MAIN_SCREEN_AD
                        )
                    }

                    BottomNavigationBar(
                        tabBarItems = bottomNavigationItem,
                        selected = selectedItem,
                        onItemClick = { index ->
                            when (index) {
                                0 -> navigateToTab(navController, Route.FollowUp.route)
                                1 -> navigateToTab(navController, Route.Deals.route)
                                2 -> navigateToTab(navController, Route.LeadList.route)
                                3 -> navigateToTab(navController, Route.Contact.route)
                                4 -> navigateToTab(navController, Route.More.route)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            val currentBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = currentBackStackEntry?.destination?.route

            when (currentRoute) {
                Route.Contact.route -> {
                    // Only show FAB if not navigated from follow-up bottom nav
                       if (!fromFollowUpBottomNav) {
                    FloatingActionButton(onClick = { navController.navigate(Route.AddOrUpdateContact.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contact")
                    }
                       }
                }

                Route.LeadList.route -> {
                    // Same logic for leads
                    if (!fromFollowUpBottomNav) {
                        FloatingActionButton(onClick = { navController.navigate(Route.AddOrUpdateLead.route) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Lead")
                        }
                    }
                }

                Route.FollowUp.route -> {
                    FloatingActionButton(onClick = { navController.navigate(Route.FollowUpAddEdit.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add FollowUp")
                    }
                }

                Route.Deals.route -> {
                    if (!fromFollowUpBottomNav) {
                        FloatingActionButton(onClick = { navController.navigate(Route.DealsAddEdit.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Deals")
                    }}
                }
            }
        }
    ) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.FollowUp.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {

            // Lead navigation starts here ---------------------------------------------------

            composable(route = Route.LeadList.route) {
                val fromFollowUp =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("current_module") == "Lead"
                BackHandler {
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }
                LeadListScreen(
                    emptyText = fromFollowUp,
                    onLeadClick = { lead ->
                        if (fromFollowUp) {
                            // If coming from follow-up screen, set the selected record and go back
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_id", lead.id
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_name", lead.name
                            )
                            fromFollowUpBottomNav = false
                            navController.popBackStack()
                        } else {
                            // Normal lead detail navigation
                            navController.navigate(Route.LeadDetail.createRoute(lead.id!!))
                        }
                    }
                )
            }

            composable(
                route = Route.LeadDetail.route,
                arguments = listOf(
                    navArgument("leadId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val leadId = backStackEntry.arguments?.getInt("leadId") ?: return@composable
                val refreshLeadId = navController.currentBackStackEntry?.savedStateHandle?.get<Int>(
                    "lead_id_to_refresh"
                )
                // Clear the refresh ID to prevent it from affecting future navigations
                if (refreshLeadId != null) {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("lead_id_to_refresh")
                }

                LeadDetailScreen(
                    leadId = refreshLeadId ?: leadId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = { lead ->
                        navController.navigate(Route.AddOrUpdateLead.createRoute(lead.id!!))
                    },
                    onConvertClick = { lead ->
                        // Navigate to create deal page when convert is clicked
                        navController.navigate(Route.DealsAddEdit.route)
                    }
                )
            }

            composable(
                route = Route.AddOrUpdateLead.route,
                arguments = listOf(
                    navArgument("leadId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val contactFromNextScreen =
                    navController.previousBackStackEntry?.savedStateHandle?.get<Contact>("selectedContact")
                val savedId = backStackEntry.savedStateHandle.get<Int>(
                    "selected_record_id"
                )

                val savedName = backStackEntry.savedStateHandle.get<String>("selected_record_name")
                val addUpdateLeadsViewModel = hiltViewModel<AddEditLeadViewModel>()

                BackHandler {
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }
                LaunchedEffect(savedId) {
                    addUpdateLeadsViewModel.onEvent(
                        AddEditLeadEvent.ContactSelected(
                            id = savedId ?: 0,
                            name = savedName ?: ""
                        )
                    )
                }

                AddOrUpdateLeadScreen(
                    onNavigateToContactSelect = {
                        navController.navigate(Route.SelectContact.route)
                    },
                    onBackClick = { savedLeadId ->
                        if (savedLeadId != null) {
                            // For new leads, update the lead ID in the previous screen's state
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "lead_id_to_refresh",
                                savedLeadId.toInt()
                            )
                        }
                        navController.popBackStack()
                        fromFollowUpBottomNav = false
                    },
                    contactFromNextScreenData = contactFromNextScreen
                )
            }

            composable(
                route = Route.SelectContact.route
            ) {
                SelectContactScreen(
                    onContactSelected = { contact ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_record_id", contact.id
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_record_name", contact.name
                        )
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            // Lead navigation ends here --------------------------------------------------------

            // Contact navigation starts here ---------------------------------------------------
            composable(route = Route.Contact.route) {
                val fromFollowUp =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("current_module") == "Contact"
                val fromDeals =
                    navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("from_deal") == true
                ContactListScreen(
                    emptyText = fromFollowUp,
                    onContactClick = { contact ->
                        if (fromFollowUp) {
                            // If coming from follow-up screen, set the selected record and go back
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_id", contact.id
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_name", contact.name
                            )
                            fromFollowUpBottomNav = false
                            navController.popBackStack()

                        } else if (fromDeals) {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_id", contact.id
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selected_record_name", contact.name
                            )
                            fromFollowUpBottomNav = false
                            navController.popBackStack()
                        } else {
                            // Normal contact detail navigation
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "selected_contact",
                                contact
                            )
                            navController.navigate(Route.ContactDetail.route)
                        }
                    }
                )
            }

            composable(Route.ContactDetail.route) {
                val contactDetail =
                    navController.previousBackStackEntry?.savedStateHandle?.get<Contact>(
                        "selected_contact"
                    )
                val refreshContactId = navController.currentBackStackEntry?.savedStateHandle?.get<Int>(
                    "contact_id_to_refresh"
                )
                // Clear the refresh ID to prevent it from affecting future navigations
                if (refreshContactId != null) {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("contact_id_to_refresh")
                }

                contactDetail?.let { contact ->
                    ContactDetailsScreen(
                        if (refreshContactId != null) Contact(id = refreshContactId, name = "", mobile = "") else contact,
                        onBackClick = { navController.popBackStack() }) { contactId ->
                        navController.navigate(Route.AddOrUpdateContact.route + "?contactId=${contactId.id}")
                    }
                }
            }
            composable(
                route = Route.AddOrUpdateContact.route + "?contactId={contactId}",
                arguments = listOf(
                    navArgument(name = "contactId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) {
                BackHandler {
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }

                AddOrUpdateContactScreen(onContactSaved = { newContactId ->
                    if (newContactId != null) {
                        // For new contacts, update the contact ID in the previous screen's state
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "contact_id_to_refresh",
                            newContactId.toInt()
                        )
                    }
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }
                )
            }
            // Contact navigation ends here ---------------------------------------------------

            // Follow Up navigation starts here -----------------------------------------------

            composable(route = Route.FollowUp.route) {
                FollowUpListScreen(
                    onNavigateToDetail = { followUpId ->
                        navController.navigate(Route.FollowUpDetail.createRoute(followUpId))
                    }
                )
            }

            composable(
                route = Route.FollowUpDetail.route,
                arguments = listOf(
                    navArgument("followUpId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                FollowUpDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Route.FollowUpAddEdit.route + "?followUpId=$id")
                    }
                )
            }
            composable(
                route = Route.FollowUpAddEdit.route + "?followUpId={followUpId}",
                arguments = listOf(
                    navArgument(name = "followUpId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->

                val savedId = backStackEntry.savedStateHandle.get<Int>(
                    "selected_record_id"
                )

                val savedName = backStackEntry.savedStateHandle.get<String>("selected_record_name")

                val addUpdateFollowUpViewModel: AddEditFollowUpViewModel = hiltViewModel()

                LaunchedEffect(savedId) {
                    addUpdateFollowUpViewModel.onEvent(
                        AddEditFollowUpEvent.RecordSelected(
                            id = savedId ?: -1,
                            name = savedName ?: ""
                        )
                    )
                }
                BackHandler {
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }

                FollowUpAddEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                        fromFollowUpBottomNav = false
                    },
                    onNavigateToRecordSelection = { module ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "current_module",
                            module
                        )
                        when (module) {
                            "Contact" -> {
                                fromFollowUpBottomNav = true
                                navController.navigate(Route.Contact.route)
                            }
                            "Lead" -> {
                                fromFollowUpBottomNav = true
                                navController.navigate(Route.LeadList.route)
                            }
                            "Deal" -> {
                                fromFollowUpBottomNav = true
                                navController.navigate(Route.Deals.route)
                            }
                        }
                    }
                )
            }
            // Follow Up navigation ends here -------------------------------------------------

            // Deals navigation starts here ---------------------------------------------------

            composable(
                route = Route.DealsAddEdit.route + "?dealId={dealId}",
                arguments = listOf(
                    navArgument(name = "dealId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->

                val savedId = backStackEntry.savedStateHandle.get<Int>(
                    "selected_record_id"
                )
                val savedName = backStackEntry.savedStateHandle.get<String>("selected_record_name")

                val addUpdateDealsViewModel = hiltViewModel<AddEditDealViewModel>()

                LaunchedEffect(savedId) {
                    addUpdateDealsViewModel.onEvent(
                        AddEditDealEvent.ContactSelected(
                            id = savedId ?: 0,
                            name = savedName ?: ""
                        )
                    )
                }
                BackHandler {
                    navController.popBackStack()
                    fromFollowUpBottomNav = false
                }

                DealAddEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                        fromFollowUpBottomNav = false
                    },
                    onNavigateToContactSelection = {
                        fromFollowUpBottomNav = true

                        navController.currentBackStackEntry?.savedStateHandle?.set("from_deal", true)

                        navController.navigate(Route.Contact.route)
                    }
                )
            }

            composable(route = Route.Deals.route) {
                val fromFollowUp =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("current_module") == "Deal"

                DealListScreen(
                    emptyText = fromFollowUp,
                    onNavigateToDealDetail = { dealId ->
                        navController.navigate(Route.DealsDetail.createRoute(dealId))
                    },
                    onDealClick = if (fromFollowUp) { deal ->
                        // If coming from follow-up screen, set the selected record and go back
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_record_id", deal.id
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_record_name", deal.title
                        )
                        fromFollowUpBottomNav = false
                        navController.popBackStack()
                    } else null
                )
            }

            composable(
                route = Route.DealsDetail.route,
                arguments = listOf(
                    navArgument("dealId") {
                        type = NavType.IntType

                    }
                )
            ) { backStackEntry ->
                DealDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Route.DealsAddEdit.route + "?dealId=$id")
                    }
                )
            }
            // Deals navigation ends here ----------------------------------------------------

            // More navigation starts here ---------------------------------------------------
            composable(route = Route.More.route) {
                MoreScreen(
                    onNavigateToCustomFields = { navController.navigate(Route.CustomFields.route) },
                    onNavigateToBackupRestore = { navController.navigate("more/backup-restore") },
                    onOpenPrivacyPolicy = {
                        // URL encode the URL to handle special characters
                        val encodedUrl = java.net.URLEncoder.encode("https://bluetoothprinter.in/privacy_policy_lfm.html", "UTF-8")
                        navController.navigate("webview/Privacy Policy/$encodedUrl")
                    },
                    onOpenTermsOfService = {
                        // URL encode the URL to handle special characters
                        val encodedUrl = java.net.URLEncoder.encode("https://bluetoothprinter.in/terms_lfm.html", "UTF-8")
                        navController.navigate("webview/Terms of Service/$encodedUrl")
                    },
                    onNavigateToPaidVersion = {
                        navController.navigate("coming_soon")
                    },
                    onOpenTechpuram = {
                        // URL encode the URL to handle special characters
                        val encodedUrl = java.net.URLEncoder.encode("https://techpuram.com", "UTF-8")
                        navController.navigate("webview/Techpuram/$encodedUrl")
                    }
                )
            }
            composable("more/backup-restore") {
                BackupRestoreScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(route = Route.CustomFields.route) {
                CustomFieldsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            // More navigation ends here ----------------------------------------------------

            composable(
                route = "webview/{title}/{url}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("url") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                val url = try {
                    URLDecoder.decode(encodedUrl, "UTF-8")
                } catch (e: Exception) {
                    encodedUrl // fallback to original if decoding fails
                }

                WebViewScreen(
                    title = title,
                    url = url,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            // Coming Soon route
            composable("coming_soon") {
                ComingSoonScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

private fun navigateToTab(
    navController: NavController,
    route: String
) {
    navController.navigate(route = route) {
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}
