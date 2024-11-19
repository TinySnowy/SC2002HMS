package pharmacy_management.inventory;

/**
 * Enumeration defining possible states for medication replenishment requests.
 * Represents:
 * - Initial request state (PENDING)
 * - Administrative decisions (APPROVED/REJECTED)
 * Used for tracking the lifecycle of replenishment requests.
 * 
 * Status Flow:
 * 1. PENDING: Initial state when request is created
 * 2. APPROVED: Request accepted, triggers inventory update
 * 3. REJECTED: Request denied, no inventory change
 */
public enum ReplenishmentStatus {
    /** 
     * Initial state of new replenishment requests.
     * Indicates request is awaiting administrative review.
     */
    PENDING,
    
    /** 
     * Request has been approved by administration.
     * Triggers inventory stock update process.
     */
    APPROVED,
    
    /** 
     * Request has been rejected by administration.
     * No changes made to inventory levels.
     */
    REJECTED
}