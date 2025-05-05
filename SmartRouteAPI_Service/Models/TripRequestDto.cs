using System.ComponentModel.DataAnnotations;

namespace TripsApi.Models
{
    public class TripRequestDto
    {
        [Required]
        [MinLength(2)]
        public List<string> Locations { get; set; } = new();

        [Required]
        [StringLength(100, MinimumLength = 1)]
        public string Name { get; set; } = "";
    }
}
