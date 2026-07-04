package online.armanportfolio.sms.dto;

import java.util.Map;

/**
 * Aggregate figures for the dashboard view: headline counts plus a
 * breakdown by course and branch, and the current top performer.
 */
public record StudentStatsResponse(
        long totalStudents,
        long totalCourses,
        long totalBranches,
        double averageClassXiiPercent,
        Map<String, Long> countByCourse,
        Map<String, Long> countByBranch,
        StudentResponse topPerformer
) {
}
