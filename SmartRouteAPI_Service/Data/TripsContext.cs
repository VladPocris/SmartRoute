using Microsoft.EntityFrameworkCore;
using TripsApi.Models;

namespace TripsApi.Data
{
    public class TripsContext : DbContext
    {
        public TripsContext(DbContextOptions<TripsContext> options) : base(options) { }
        public DbSet<Trip> Trips { get; set; }
        public DbSet<TripStep> TripSteps { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Trip>()
                .HasIndex(t => t.Code)
                .IsUnique();

            modelBuilder.Entity<Trip>()
                .HasIndex(t => t.OriginalQueryKey);
        }
    }
}
