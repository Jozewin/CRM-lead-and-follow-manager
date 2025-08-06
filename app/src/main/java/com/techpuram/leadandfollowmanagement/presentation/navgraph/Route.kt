package com.techpuram.leadandfollowmanagement.presentation.navgraph

sealed class Route(
    val route: String
) {

    data object OnBoardingScreen : Route(route = "onBoardingScreen")
    data object AppNavigatorScreen : Route(route = "appNavigatorScreen")

    // Bottom Navigation
    data object FollowUp : Route(route = "followUp")
    data object Lead : Route(route = "lead")
    data object Contact : Route(route = "contact")
    data object Deals : Route(route = "deals")
    data object More : Route(route = "more")

    // Lead Screen ----------------------------------------------
    data object LeadList : Route(route = "leadList")
    data object LeadDetail : Route(route = "leadDetail/{leadId}") {
        fun createRoute(leadId: Int) = "leadDetail/$leadId"
    }
    data object AddOrUpdateLead : Route(route = "addOrUpdateLead?leadId={leadId}") {
        fun createRoute(leadId: Int = -1) = "addOrUpdateLead?leadId=$leadId"
    }
    object SelectContact : Route(route = "selectContact")
    // End of Lead Screen ---------------------------------------

    // Contact Screen -------------------------------------------
    data object ContactDetail : Route(route = "contactDetail")
    data object AddOrUpdateContact : Route(route = "addOrUpdateContact")
    // End of Contact Screen ------------------------------------

    // More Screen ----------------------------------------------
    data object CustomFields : Route(route = "customFields")
    // End of More Screen ---------------------------------------

    // Follow up Screen -----------------------------------------
    data object FollowUpAddEdit : Route(route = "followUpAddEdit")
    data object FollowUpDetail : Route(route = "followUpDetail/{followUpId}") {
        fun createRoute(followUpId: Int) = "followUpDetail/$followUpId"
    }

    // Deals Screen
    data object DealsAddEdit : Route(route = "dealsAddEdit")
    data object DealsDetail : Route(route = "dealsDetail/{dealId}") {
        fun createRoute(dealId: Int) = "dealsDetail/$dealId"
    }

    // End of Follow up Screen ----------------------------------

    // Sub-Navigation
    data object AppStartNavigation : Route(route = "appStartNavigation")
    data object AppNavigation : Route(route = "appNavigation")



}