using System.Threading.Tasks;
using TripsApi.Services.Google;

namespace TripsApi.Services
{
    public interface IRoutesClient
    {
        Task<ComputeRoutesResponse> OptimizeRouteAsync(string[] stops);
    }
}
