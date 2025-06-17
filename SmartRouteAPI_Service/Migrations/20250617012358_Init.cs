using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace SmartRouteAPI_Service.Migrations
{
    /// <inheritdoc />
    public partial class Init : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Trips",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Code = table.Column<string>(type: "character varying(6)", maxLength: 6, nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    OriginalQuery = table.Column<string>(type: "text", nullable: true),
                    OriginalQueryKey = table.Column<string>(type: "text", nullable: false),
                    OverviewPolyline = table.Column<string>(type: "text", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Trips", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "TripSteps",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    TripId = table.Column<int>(type: "integer", nullable: false),
                    From = table.Column<string>(type: "text", nullable: false),
                    To = table.Column<string>(type: "text", nullable: false),
                    DistanceKm = table.Column<double>(type: "double precision", nullable: false),
                    DurationMinutes = table.Column<int>(type: "integer", nullable: false),
                    Order = table.Column<int>(type: "integer", nullable: false),
                    StartLat = table.Column<double>(type: "double precision", nullable: false),
                    StartLng = table.Column<double>(type: "double precision", nullable: false),
                    EndLat = table.Column<double>(type: "double precision", nullable: false),
                    EndLng = table.Column<double>(type: "double precision", nullable: false),
                    EncodedPolyline = table.Column<string>(type: "text", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TripSteps", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TripSteps_Trips_TripId",
                        column: x => x.TripId,
                        principalTable: "Trips",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Trips_Code",
                table: "Trips",
                column: "Code",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_Trips_OriginalQueryKey",
                table: "Trips",
                column: "OriginalQueryKey");

            migrationBuilder.CreateIndex(
                name: "IX_TripSteps_TripId",
                table: "TripSteps",
                column: "TripId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "TripSteps");

            migrationBuilder.DropTable(
                name: "Trips");
        }
    }
}
