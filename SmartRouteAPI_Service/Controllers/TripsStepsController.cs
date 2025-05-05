using System.Linq;
using Microsoft.AspNetCore.Mvc;
using TripsApi.Data;
using TripsApi.Models;

namespace TripsApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TripStepsController : ControllerBase
    {
        private readonly TripsContext context;

        public TripStepsController(TripsContext context)
        {
            this.context = context;
        }

        // GET /api/tripsteps
        [HttpGet]
        public IActionResult GetAll()
        {
            var steps = context.TripSteps
                .OrderBy(s => s.Id)
                .ToList();
            return Ok(steps);
        }

        // GET /api/tripsteps/code/{code}
        [HttpGet("code/{code}")]
        public IActionResult GetByTripCode(string code)
        {
            var trip = context.Trips
                .SingleOrDefault(t => t.Code == code);
            if (trip == null)
                return NotFound();

            var steps = context.TripSteps
                .Where(s => s.TripId == trip.Id)
                .OrderBy(s => s.Order)
                .ToList();
            return Ok(steps);
        }
    }
}
